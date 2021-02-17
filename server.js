#!/usr/bin/env node
const Koa = require('koa');
const app = new Koa();
const port = process.env['PORT'] || 3000;
// const bodyParser = require('koa-body');
const bodyParser = require('koa-bodyparser');
const send = require('koa-send');
const serve = require('koa-static');
const fetch = require('node-fetch');

// logger
app.use(async (ctx, next) => {
    await next();
    const rt = ctx.response.get('X-Response-Time');
    console.log(`${ctx.method} ${ctx.url} - ${rt}`);
});

// response time
app.use(async (ctx, next) => {
    const start = Date.now();
    await next();
    const ms = Date.now() - start;
    ctx.set('X-Response-Time', `${ms}ms`);
});

// static response
app.use(serve('public', { extensions: true }));

// parser
app.use(bodyParser());

// reactive response
app.use(async (ctx) => {
    console.log('Request: ', ctx.request);

    if (ctx.path.match(/\/search/) !== null) {

      let esRequestURI = "http://52.52.217.209:9200" + ctx.path.replace("/search", "");

      await (async function main (uri, query) {
        console.log(uri, query);

        return (new Promise(async resolve => {
          try {
            fetch(uri, {
              method: 'POST',
              headers: {
                'content-type': 'application/json;utf-8'
              },
              body: JSON.stringify(query)
            })
              .then(res => res.json())
              .then(json => {
                // console.log(json);
                resolve({ result: json });
              });

          } catch (e) {
            console.error(e);
            resolve('{ "error": ' + JSON.stringify(e) +'}');
          }
        }));

      })(esRequestURI, ctx.request.body)
        .then(r => {
          // console.debug(JSON.stringify(r)); // debug
          ctx.header['Content-Type'] = 'application/json';
          ctx.body = r;
        }, err => {
          console.error(err);
        });

    } else switch (ctx.path) {

        case ('/log.json'):
            await send(ctx, '/static/log.json');
            break;

        default:
            return ctx.body = 'Not Found';
    }
});


app.listen(port);

console.log('Listening @ http://127.0.0.1:' + port);
