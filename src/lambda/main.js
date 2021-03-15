const aws = require('aws-sdk');
const s3 = new aws.S3({ apiVersion: '2006-03-01' });

const buildName = "sdp-statistics";

const execDelay = 850333; // time until EC2 boots up; lambda limit of 15 min

const res/*: { statusCode: any, body: any }*/ = {
    statusCode: 400,
    body: {}
};

let run_id = 0;

let setExecTimeout = false;

export default async (event, context, callback)/*: Promise<{ statusCode: any, body: any }>*/ => {

    const currentTime = (new Date().getTime());

    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        if (run_id++ > 0) {
            console.error("Attemping to run function more than once per process: ", run_id);
            console.log("Attemping to run function more than once per process: ", run_id);
            throw new Error("Attemping to run function more than once per process");
        }

        console.log('Received event:', JSON.stringify(event, null, 2));

        // Get the object from the event and show its content type
        const records = (!!event.Records && !!event.Records.length) ?
            event.Records :
            event.body.Records;
        const bucket = records[0].s3.bucket.name;
        const key = decodeURIComponent(records[0].s3.object.key.replace(/\+/g, ' '));

        // Set times
        try {
            res.body['initTime'] = new Date(
                (records[0]['eventTime'].match(/Z$/).length > 0) ?
                    records[0]['eventTime'].slice(0, -8) :
                    records[0]['eventTime']
            );
        } catch (e) {
            res.body['initTime'] = new Date();
        }

        const buildTicketParams = {
            Bucket: bucket,
            Key: 'Pitney_Bowes/InProcess/StreetPro/ReleaseStatistics/logs/buildStreetProStats.json',
        };

        try {

            const buildTicket = await s3.getObject(buildTicketParams).promise();

            if (!!buildTicket['Body'] && !!buildTicket['LastModified']) {
                console.log("Ticket was last modified ", new Date(buildTicket['LastModified']));
                console.log(buildTicket['Body'].toString('utf-8'));

                const body = JSON.parse(buildTicket['Body'].toString('utf-8'));
                const releaseTimeout = (!!body['execTime']) ?
                    (new Date(body['execTime'])).getTime() :
                    (new Date(buildTicket['object']['LastModified'])).getTime() + execDelay;

                console.log("Build lock release(d) at ", new Date(releaseTimeout), releaseTimeout);
                console.log("Current time is ", new Date(), currentTime);

                if (currentTime < releaseTimeout) {
                    console.log("No updates to build ticket.");
                    res.statusCode = 200;
                    res.body = body;
                    return (new Promise(async resolve => resolve(res)));

                } else {
                    setExecTimeout = true;
                }

            } else {
                console.log("Missing build ticket or last modified params!");
                setExecTimeout = false;
            }

        } catch (e) {
            const message = `Error getting object ${key} from bucket ${bucket}.`;
            console.error(message, e);

            setExecTimeout = false;
        }

        const requestParams = {
            Bucket: bucket,
            Key: key,
        };

        console.log("Request S3 object: ", requestParams);

        if (setExecTimeout) try {
            const objectMeta = await s3.getObject(requestParams).promise();
            console.log('CONTENT TYPE:', objectMeta.ContentType);

            res.body['initTime'] = (res.body['initTime'] !== null) ?
                res.body['initTime'] :
                new Date(objectMeta['LastModified']);
            res.body['execTime'] = new Date(res.body['initTime'].getTime() + execDelay);

            console.log("Should boot EC2 at ", res.body['execTime']);

            res.body['object'] = objectMeta;
            res.body['object']['Bucket'] = bucket;
            res.body['object']['Key'] = key;
            res.body['object']['Body'] = null;

            buildTicketParams.Body = JSON.stringify(res.body, null, 2);

            // Log trigger to build StreetPro stats
            await s3.putObject(buildTicketParams, function(err, data) {
                    if (err) console.error(err, err.stack); // an error occurred
                    // else     console.log(data);           // successful response
                })
                .promise()
                .then(data => {
                    //console.log(data);

                    res.statusCode = 200;
                })
                .catch(err => {
                    console.error(err, err.stack);
                    res.body = err;
                    res.headers = headers;

                    res.statusCode = 500;
                });

            if (res.statusCode === 200) {
                console.log("Update build ticket exec time: ", res.body['execTime']);

                return (new Promise/*<{ statusCode: any, body: any }>*/(resolve => setTimeout(async () => {
                    // Create EC2 service object
                    const ec2 = new aws.EC2({ apiVersion: "2016-11-15" });

                    var instanceQueryParams = {
                        Filters: [
                            {
                                Name: "tag:Name",
                                Values: [
                                    buildName,
                                    buildName + "-test"
                                ]
                            }
                        ]
                    };

                    var launchNewInstance = true;

                    ec2.describeInstances(instanceQueryParams, async function(err, data) {
                        if (err) console.error(err, err.stack);   // an error occurred
                        else {                                    // successful response

                            for (let reservation of data['Reservations']) {
                                //console.log(reservation);

                                if (!!reservation['Instances']) {
                                    const instances = reservation['Instances'];
                                    for (let i of instances) {

                                        //console.log(i["Tags"]);

                                        if (i['Tags'].length > 0 && i['Tags'].filter(t => t['Value'].match(buildName) !== null).length > 0) {
                                            console.log('Found instance(s) with tag: ', i['InstanceId']);

                                            await ec2.terminateInstances({
                                                InstanceIds: [
                                                    i['InstanceId']
                                                ]
                                            }, function (err, data) {
                                                if (err) { // an error occurred
                                                    console.log('Failed to remove existing instance: ', i['InstanceId']);
                                                    console.error(err, err.stack);
                                                    launchNewInstance = false;
                                                } else { // successful response
                                                    console.log('Terminated instance: ', i['InstanceId']);
                                                    console.log(data);
                                                }
                                            });
                                        }
                                    }

                                }
                            }

                            if (!!launchNewInstance) {

                                console.log('Ready to launch EC2!', res.body);

                                const commands = [
                                    '#!/usr/bin/env bash',
                                    'echo "build release statistics @ $(date)" >> lambda-run.log',
                                    'cd /home/ec2-user/data/_statistics',
                                    'exec 1<>lambda-run.log',
                                    'exec 2>&1',
                                    '$(aws ecr get-login --no-include-email --region=us-east-1)',
                                    'docker run -d --mount type=bind,source="$(pwd)",destination=/data --privileged 450076028976.dkr.ecr.us-east-1.amazonaws.com/mro-3.5.1 bash /data/docker/build-stats.sh',
                                    'sleep 2400',
                                    'chown -R ec2-user ./*',
                                    'touch ~/.aws/config',
                                    'echo "[profile ecs]" >> ~/.aws/config',
                                    'echo "region = us-east-1" >> ~/.aws/config',
                                    'echo "output = json" >> ~/.aws/config',
                                    'echo "role_arn = arn:aws:iam::450076028976:role/aws-ecs-role" >> ~/.aws/config',
                                    'echo "source_profile = default" >> ~/.aws/config',
                                    "export instanceid=$(curl 'http://169.254.169.254/latest/meta-data/instance-id')",
                                    // 'echo $(aws --profile=ecs ec2 terminate-instances --instance-ids $instanceid --region us-east-1)',
                                    "exit"
                                ];

                                // AMI (ami-0ab408891fffe6a27)
                                const instanceParams = {
                                    ImageId: 'ami-02b731ed75b1ed711',
                                    InstanceType: 't2.small',
                                    IamInstanceProfile: {
                                        Name: 'aws-ecs-role'
                                    },
                                    InstanceInitiatedShutdownBehavior: 'terminate',
                                    KeyName: 'sdp-prod',
                                    MinCount: 1,
                                    MaxCount: 1,
                                    SubnetId: 'subnet-ac2a35c8',
                                    SecurityGroupIds: [
                                        'sg-e90d399f',
                                        'sg-34d8ef42'
                                    ],
                                    UserData: new Buffer(commands.join("\n")).toString('base64')
                                };

                                // Create a promise on an EC2 service object
                                const instancePromise = ec2
                                    .runInstances(instanceParams).promise();

                                // Handle promise's fulfilled/rejected states
                                instancePromise.then(function (data) {
                                    console.log(data);
                                    const instanceId = data.Instances[0].InstanceId;

                                    console.log("Created instance", instanceId);

                                    res.statusCode = 200;
                                    res.body['instance'] = data.Instances[0];

                                    // Add tags to the instance
                                    const tagParams = {
                                        Resources: [instanceId], Tags: [
                                            {
                                                Key: 'Name',
                                                Value: buildName
                                            }
                                        ]
                                    };

                                    // Create a promise on an EC2 service object
                                    const tagPromise = new aws.EC2({apiVersion: '2016-11-15'}).createTags(tagParams).promise();

                                    // Handle promise's fulfilled/rejected states
                                    tagPromise.then(function (data) {
                                        console.log("Instance tagged: ", tagParams.Tags[0].Value);

                                        res.statusCode = 200;

                                        try {

                                            // prevent launching any additional EC2 machines for 2 x execDelay (around 45 minutes)
                                            const body = JSON.parse(buildTicketParams.Body.toString('utf-8'));

                                            console.log('Extending build delay from ' + currentTime + '...');
                                            body['execTime'] = new Date(currentTime + (execDelay * 2));

                                            console.log('... to ' + body['execTime'].getTime());

                                            buildTicketParams.Body = JSON.stringify(body, null, 2);

                                            s3.putObject(buildTicketParams, function (err, data) {
                                                    if (err) console.log("Error", err, err.stack); // an error occurred
                                                    else console.log(data);           // successful response
                                                })
                                                .promise()
                                                .then(data => {
                                                    console.log(data);

                                                    console.log('Launched EC2 after original eventTime:');
                                                    console.log(records[0]['eventTime']);
                                                    console.log(new Date(records[0]['eventTime']));

                                                    resolve(res);
                                                })
                                                .catch(err => {
                                                    console.log(err, err.stack);
                                                    res.body = err;
                                                    res.headers = headers;

                                                    res.statusCode = 500;

                                                    resolve(res);
                                                });

                                        } catch (e) {
                                            resolve(res);
                                        }

                                    })
                                        .catch(function (err) {
                                            console.error(err, err.stack);
                                            res.body = err;
                                            res.headers = headers;

                                            resolve(res);
                                        });

                                })
                                .catch(function (err) {
                                    console.error(err, err.stack);
                                    res.body = err;
                                    res.headers = headers;

                                    resolve(res);
                                });
                            }
                        }
                    });

                }, execDelay)));

            } else {
                console.log("No updates to build ticket.");
                return (new Promise(async resolve => resolve(res)));
            }


        } catch (err) {
            const message = `Error getting object ${key} from bucket ${bucket} OR creating instance.`;
            console.log(message, err);
            res.body = new Error(message);
            res.headers = headers;

            return (new Promise(async resolve => resolve(res)));

        }  else {
            console.log("No updates to build ticket.");
            return (new Promise(async resolve => resolve(res)));
        }


    } catch (err) {
        console.error(err);
        res.statusCode = 500;
        res.headers = headers;
        res.body = {
            Error: err.toString()
        };

        return (new Promise(async resolve => resolve(res)));
    }
}

