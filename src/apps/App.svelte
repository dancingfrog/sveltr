<script>
    import {onMount} from 'svelte';
    import ChartPanel from './components/ChartPanel.svelte';
    import carto from '@carto/carto-vl';
    import mapboxgl from 'mapbox-gl';

    let map;
    let mapBounds = [
      [ -80.83415919532996, 39.98043619521454 ],
      [ -73.45134669532756, 35.74110771792324 ]
    ];
    let fips = "";
    let name = "";
    let state = "";
    let covid_deaths = "";
    let pop = "";

    let chartData =  [];

    let chartTitle = "Covid-19 Death Rates v.s DoD Rates"
    let chartSubtitle = "Pan or Zoom Map to Refresh Charts"
    let boundsTitle = ""

    let sinceLastSearchQuery = 0;

    let esFeatureQuery = (bounds, query, cause_index) => {

      const cause_indices = (!!cause_index) ?
          [ cause_index ] :
          [
            'death_by_alcohol_data',
            'death_by_cirrhosis_data',
            'death_by_drug_data',
            'death_by_suicide_data',
            'death_by_dod_data'
          ];

      if (!!sinceLastSearchQuery && ((new Date()).getTime() - sinceLastSearchQuery) < 533) {
        return;
      } else {
        sinceLastSearchQuery = (new Date()).getTime();
      }

      const newChartData = [];

      cause_indices.forEach(cause_index => {

        let esRequestURI = `/search/${cause_index}/_search`
        const body = {};

        body['aggs'] = {
          "avg_grade": {
            "avg": {
              "field": "deaths_per_100k"
            }
          }
        };

        body['fields'] = [ "cartodb_id", "fid", "geoid_co", "name", "st_name", "lon","lat", "time_period", "crude_rate", "death_cause", "population", "deaths_dod", "pop", "deaths_per_100k" ];

        query["bool"]["filter"] = {
          "shape": {
            "geom_box": {
              "shape": {
                "type": "envelope",
                "coordinates" : bounds
              },
              "relation": "intersects"
            }
          }
        };

        query["bool"]["must"] = [
            {
              // {
              //   "term": { "time_period": group }
              // },
              "range": {
                "crude_rate": {
                  "gte": (
                    (cause_index === 'death_by_alcohol_data') ? alc_rate :
                      (cause_index === 'death_by_cirrhosis_data') ? crh_rate :
                        (cause_index === 'death_by_drug_data') ? drg_rate:
                          (cause_index === 'death_by_suicide_data') ? sui_rate :
                            dod_rate
                  ) - 1 // small compensation
                }
              }
            }
        ];

        body['query'] = query;

        body['size'] = 1000;

        body['_source'] = false;

        // console.log(body);

        fetch(esRequestURI, {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(body)
        })
          .then(res => res.json())
          .then(json => {
            console.log(json);
            chartSubtitle = group + " records for counties within window bounds ";
            boundsTitle = "(" + bounds[0][0].toFixed(3) + " " + bounds[0][1].toFixed(3) + ", " + bounds[1][0].toFixed(3) + " " + bounds[1][1].toFixed(3) + ")";
            // console.log(cause_index);
            const newChart = {
              name:
                (cause_index === 'death_by_alcohol_data') ? "Alcohol Death Rate (per 100k)" :
                  (cause_index === 'death_by_cirrhosis_data') ? "Cirrhosis Death Rate (per 100k)" :
                    (cause_index === 'death_by_drug_data') ? "Drug Death Rate (per 100k)" :
                      (cause_index === 'death_by_suicide_data') ? "Suicide Death Rate (per 100k)" :
                        "Deaths of Despair Rate (per 100k)"
            };
            newChart['aggregations'] = json['result']['aggregations'];
            newChart['points'] = json['result']['hits']['hits'].map(county => {
              try {
                county['x'] = county['fields']['crude_rate'][0];
                county['y'] = county['fields']['deaths_per_100k'][0];
              } catch (e) {
                // console.log("'crude_rate' missing from ", county['fields']);
              }
              return county;
            });
            newChartData.push(newChart);
            chartData = newChartData.sort((a, b) => {
                return (a['name'].match(/Despair/) !== null) ? 1 :
                  (b['name'].match(/Despair/) !== null) ? -1 :
                    (a['name'] > b['name']) ? 1 :
                      0;
              }
            );
            // console.log(newChartData);
          });
      });


    };

    let handleFeatureSearch = (evt) => {
      // console.log(evt);
      const data = evt['explicitOriginalTarget'].getAttribute('data');
      try {
        // console.log(JSON.parse(data));
        const county_source = JSON.parse(data)['fields'];
        fips = county_source.geoid_co[0];
        name = county_source.name[0];
        state = county_source.st_name[0];
        covid_deaths = county_source.deaths_per_100k[0].toFixed(0);
        pop = county_source.pop[0].toFixed(0);
        console.log(`You have clicked on ${name}, ${state} (${fips}) with a population of ${pop}`);
        console.log(county_source['deaths_per_100k'])

      } catch (e) {
        console.error(data);
      }
    }

    let dod_color = '#c80e38';
    let dod_rate = 50;
    let group = '2014-2018';

    let changeDodColor = (color) => {
      console.log("Set color for Deaths of Despair ", color)
    };
    $: changeDodColor(dod_color);

    let changeDodRate = (rate) => {
      console.log("Set minimum rate for Deaths of Despair ", rate)
    };
    $: changeDodRate(dod_rate);

    let changeTimePeriod = (group) => {
      console.log("Set time period of dataset ", group)
    };
    $: changeTimePeriod(group);


    let alc_color = '#1b7ccb';
    let alc_rate = 0;

    let changeAlcColor = (color) => {
        console.log("Set color for Deaths by Alcohol ", color)
    };
    $: changeAlcColor(alc_color);

    let changeAlcRate = (rate) => {
        console.log("Set minimum rate for Deaths by Alcohol ", rate)
    };
    $: changeAlcRate(alc_rate);


    let crh_color = '#5b3ccb';
    let crh_rate = 10;

    let changeCrhColor = (color) => {
        console.log("Set color for Deaths by Cirrhosis ", color)
    };
    $: changeCrhColor(crh_color);

    let changeCrhRate = (rate) => {
        console.log("Set minimum rate for Deaths by Cirrhosis ", rate)
    };
    $: changeCrhRate(crh_rate);

    let drg_color = '#ff013c';
    let drg_rate = 20;

    let changeDrgColor = (color) => {
        console.log("Set color for Deaths by Drug ", color)
    };
    $: changeDrgColor(drg_color);

    let changeDrgRate = (rate) => {
        console.log("Set minimum rate for Deaths by Drug ", rate)
    };
    $: changeDrgRate(drg_rate);


    let sui_color = '#0aecd1';
    let sui_rate = 25;

    let changeSuiColor = (color) => {
        console.log("Set color for Deaths by Suicide ", color)
    };
    $: changeSuiColor(sui_color);

    let changeSuiRate = (rate) => {
        console.log("Set minimum rate for Deaths by Suicide ", rate)
    };
    $: changeSuiRate(sui_rate);

    onMount(() => {
        let frame;

      // "LngLat(-72.92534844759798, 40.02120870764847), LngLat(-80.30816094760172, 35.78429715317695)"

        console.log(carto.basemaps.darkmatter);
        const map = new mapboxgl.Map({
            container: 'map',
            style: carto.basemaps.darkmatter,
            center: [-77, 37.9],
            zoom: 7,
            scrollZoom: true
        });
        const nav = new mapboxgl.NavigationControl({
            showCompass: false
        });
        map.addControl(nav, 'top-left');
        map.addControl(new mapboxgl.FullscreenControl(), 'top-left');

      map.on('move', function() {
        window.bounds = map.getBounds();
        // Example
        // {
        //   "_sw": {
        //   "lng": -80.9355207272108,
        //     "lat": 35.38870537135182
        // },
        //   "_ne": {
        //   "lng": -72.49775691763645,
        //     "lat": 40.23623113003447
        // }
        // }

        mapBounds = [
          [  window.bounds['_sw']['lng'], window.bounds['_ne']['lat'] ],
          [  window.bounds['_ne']['lng'], window.bounds['_sw']['lat'] ]
        ];
        console.log('A move event occurred: ', mapBounds);
        esFeatureQuery(
          mapBounds,
          {
            "bool": {
              // "must": {
              //   "match_all": {}
              // }
              "should": [{
                "match": {
                  "time_period": {
                    "query": group,
                    "auto_generate_synonyms_phrase_query" : true
                  }
                }
              }],
              "minimum_should_match": 1
            }
          }
        );
      });


      // Define user
        carto.setDefaultAuth({
          apiKey: 'default_public',
          username: 'dancingfrog'
        });

        let layerA = {};
        let layerB = {};
        let removeDelayA = {};
        let removeDelayB = {};
        let currentLayer = {
          'DoD': 'DoDA',
          'Alcohol': 'AlcoholA',
          'Cirrhosis': 'CirrhosisA',
          'Drug': 'DrugA',
          'Suicide': 'SuicideA'
        };
        let init = {};

        let sinceLastRateChange = {};

        const fetchDoD = (color, rate, time_period, cause) => {
            console.log(!!init[cause]);
            const style_ramp = `ramp(linear($crude_rate,1,70), [${color}, gold])`;

            if (!!sinceLastRateChange[cause] && ((new Date()).getTime() - sinceLastRateChange[cause]) < 533) {
                if (dod_color === color && dod_rate === rate && group === time_period) {
                    setTimeout(async () => {
                        // If updates coming in fast, delay and check for value consistency
                        if (
                          (dod_color === color || alc_color === color || crh_color === color || drg_color === color || sui_color === color) &&
                          (dod_rate === rate || alc_rate === rate || crh_rate === rate || drg_rate === rate || sui_rate === rate) &&
                          group === time_period
                        ) {
                            clearTimeout(removeDelayA[cause]);
                            clearTimeout(removeDelayB[cause]);
                        }
                    }, 333);
                }
                return;

            } else if (!!init[cause]) {
              const viz2 = new carto.Viz(`
                  @style: opacity(${style_ramp}, 0.05)
                  color: @style
                  width: 1
                `);

              if (currentLayer[cause] === cause + 'A' && !!layerA[cause] && layerA[cause].hasOwnProperty("id")) {
                // Fade out
                layerA[cause].viz.color.blendTo(`opacity(${style_ramp}, 0.05)`)
                removeDelayA[cause] = setTimeout(() => {
                  if (currentLayer[cause] !== 'A') {
                    console.log("Removing layer " + cause + "A");
                    layerA[cause].remove();
                  }
                }, 533);
                currentLayer[cause] = cause + 'B';

              } else if (!!layerB[cause] && layerB[cause].hasOwnProperty("id")) {
                layerA[cause].viz.color.blendTo(`opacity(${style_ramp}, 0.05)`)
                removeDelayB[cause] = setTimeout(() => {
                  if (currentLayer[cause] !== 'B') {
                    console.log("Removing layer " + cause + "B");
                    layerB[cause].remove();
                  }
                }, 533);
                currentLayer[cause] = cause + 'A';
              }
              sinceLastRateChange[cause] = (new Date()).getTime();

              esFeatureQuery(
                mapBounds,
                {
                  "bool": {
                    "should": [ {
                      "match": {
                        "time_period": {
                          "query": group,
                          "auto_generate_synonyms_phrase_query": true
                        }
                      }
                    } ],
                    "minimum_should_match": 1
                  }
                  // },
                  // (cause === 'Alcohol') ? 'death_by_alcohol_data' :
                  //   (cause=== 'Cirrhosis') ? 'death_by_cirrhosis_data' :
                  //     (cause === 'Drug') ? 'death_by_drug_data' :
                  //       (cause === 'Suicide') ? 'death_by_suicide_data' :
                  //         'death_by_dod_data'
                }
              )

            } else {
                sinceLastRateChange[cause] = (new Date()).getTime();
            }

            const sql_query = `select cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,time_interval,time_period,crude_rate,death_cause,population,deaths_dod,deaths_per_100k,pop,the_geom_webmercator from "ruralinnovation-admin".dod_covid_county where death_cause ilike '${cause}' and time_period ilike '${time_period}' and crude_rate > ${rate}`;
            console.log(sql_query);

            const viz1 = new carto.Viz(`
              @currentFeatures: viewportCount()
              @fips: $geoid_co
              @name: $name
              @state: $st_name
              @deaths: $deaths_per_100k
              @pop: $pop
              @style: opacity(${style_ramp}, 0.05)
              color: @style
              strokeColor: @style
            `);
            // filter: animation(linear($crude_rate,144.5, 1.5),5,fade(0,100))+0.1

          const enterFeature =  featureEvent => {
            featureEvent.features.forEach((feature) => {
              feature.color.blendTo(`opacity(${style_ramp}, 1.0)`, 100);
              feature.width.blendTo(20, 100);
            });
          };

          const exitFeature = featureEvent => {
            featureEvent.features.forEach((feature) => {
              feature.color.reset();
              feature.width.reset();
            });
          };

          const clickFeature =  featureEvent => {
            featureEvent.features.forEach((feature) => {
              console.log(feature);
              fips = feature.variables.fips.value;
              name = feature.variables.name.value;
              state = feature.variables.state.value;
              covid_deaths = feature.variables.deaths.value.toFixed(0);
              pop = feature.variables.pop.value.toFixed(0);
              console.log(`You have clicked on ${name}, ${state} (${fips}) with a population of ${pop}`);
            });
          }


          try {
                if (!!init[cause] && !!layerA[cause] && !!layerA[cause]) {
                    if (currentLayer[cause] === cause + 'A') {
                        clearTimeout(removeDelayA[cause]);
                    } else {
                        clearTimeout(removeDelayB[cause])
                    }
                    map.removeLayer(currentLayer[cause]); // immediately clear next layer
                }
            } finally {

                const dod_covid_county_source = new carto.source.SQL(
                    sql_query,
                    {
                        apiKey: 'default_public',
                        username: 'dancingfrog'
                    },
                    {
                        serverURL: 'https://ruralinnovation-admin.carto.com'
                    }
                );

                console.log("Adding layer ", currentLayer[cause]);

                if (currentLayer[cause] === cause + 'A') {
                    layerA[cause] = new carto.Layer(currentLayer[cause], dod_covid_county_source, viz1);
                    layerA[cause].addTo(map);
                    window.layers = layerA;

                    layerA[cause].on('loaded', async () => {
                        // Fade In
                        layerA[cause].viz.color.blendTo(`opacity(${style_ramp}, 0.75)`);
                        document.getElementById('loader').style.opacity = '0';
                        setTimeout(() => {
                            init[cause] = true;
                        }, 333);

                      const interactivity = new carto.Interactivity(layerA[cause]);

                      interactivity.on('featureEnter', enterFeature);
                      interactivity.on('featureLeave', exitFeature);
                      interactivity.on('featureClick', clickFeature);
                    });

                } else if (currentLayer[cause] === cause + 'B') {
                    layerB[cause] = new carto.Layer(currentLayer[cause], dod_covid_county_source, viz1);
                    layerB[cause].addTo(map);
                    window.layers = layerB;

                    layerB[cause].on('loaded', async () => {
                        // Fade In
                        layerB[cause].viz.color.blendTo(`opacity(${style_ramp}, 0.75)`);
                        document.getElementById('loader').style.opacity = '0';
                        setTimeout(() => {
                            init[cause] = true;
                        }, 333);

                      const interactivity = new carto.Interactivity(layerB[cause]);

                      interactivity.on('featureEnter', enterFeature);
                      interactivity.on('featureLeave', exitFeature);
                      interactivity.on('featureClick', clickFeature);
                    });
                }
            }

        }

        fetchDoD(dod_color, dod_rate, group, 'DoD');

        changeDodColor = (color) => fetchDoD(color, dod_rate, group, 'DoD');

        changeDodRate = (rate) => fetchDoD(dod_color, rate, group, 'DoD');

        fetchDoD(alc_color, alc_rate, group, 'Alcohol');

        changeAlcColor = (color) => fetchDoD(color, alc_rate, group, 'Alcohol');

        changeAlcRate = (rate) => fetchDoD(alc_color, rate, group, 'Alcohol');

        fetchDoD(crh_color, crh_rate, group, 'Cirrhosis');

        changeCrhColor = (color) => fetchDoD(color, crh_rate, group, 'Cirrhosis');

        changeCrhRate = (rate) => fetchDoD(crh_color, rate, group, 'Cirrhosis');

        fetchDoD(drg_color, drg_rate, group, 'Drug');

        changeDrgColor = (color) => fetchDoD(color, drg_rate, group, 'Drug');

        changeDrgRate = (rate) => fetchDoD(drg_color, rate, group, 'Drug');

        fetchDoD(sui_color, sui_rate, group, 'Suicide');

        changeSuiColor = (color) => fetchDoD(color, sui_rate, group, 'Suicide');

        changeSuiRate = (rate) => fetchDoD(sui_color, rate, group, 'Suicide');

        changeTimePeriod = (time_period) => {
          console.log("Set time period of dataset ", group);
          fetchDoD(dod_color, dod_rate, time_period, 'DoD');
          fetchDoD(alc_color, alc_rate, time_period, 'Alcohol');
          fetchDoD(crh_color, crh_rate, time_period, 'Cirrhosis');
          fetchDoD(drg_color, drg_rate, time_period, 'Drug');
          fetchDoD(sui_color, sui_rate, time_period, 'Suicide');
        }

        (function loop() {
            frame = requestAnimationFrame(loop);

            /** Animate stuff here **/
        }());

        return () => {
            cancelAnimationFrame(frame);
        };
    });
</script>

<style>
    canvas {
        width: 100vh;
        height: 100%;
        background-color: #666;
        -webkit-mask: url("../images/svelte-logo-mask.svg") 50% 50% content-box view-box no-repeat;
        mask: url("../images/svelte-logo-mask.svg") 50% 50% content-box view-box no-repeat;
    }

    div#map * canvas {
        left: 0;
    }

    .controls.right.toolbox {
        bottom: 24px;
        left: auto;
        right: 24px;
        width: 350px;
        max-width: 350px;
        margin: 0rem 0 auto 0;
    }

    .controls .box header *,
    .controls .box section * {
        text-align: left;
        text-shadow: none;
    }

    .controls .box section h3,
    .controls .box section h4,
    .controls .box section h5 {
        margin: 6px 0px;
    }

    .controls .box label {
        font-size: 1em;
    }

    .controls .box label .minimum_rate {
        font-size: 0.75em;
    }

    .controls .box label input[type="color"] {
      float: left;
      height: 18px;
      width: 24px;
      margin: 10px 2px 10px;
      padding: 2px;
    }
</style>

<div id="map"></div>
<aside class="controls right toolbox">
    <div class="box">
        <header>
            <h1>Deaths of Despair</h1>
        </header>
        <section>
            <p class="description open-sans">Explore potential correlations between
                historic mortality rates and the Covid-19 death rate in the U.S. in 2020.
                One way to “drill down” into those relationships is to filter the Deaths of Despair
                mortality data that is available per location (county or metropolitan area) by
                modulating the minimal death rate that is displayed for each cause of death and
                noting the communities that remain visible as the data reveals more desperate and
                intractable circumstances.</p>
            <h5>County: {name}, {state}</h5>
            <h5>Population (2019): {pop}</h5>
            <h5>Covid-19 Deaths: {covid_deaths} (Rate per 100k)</h5>
        </section>
        <hr/>

        <h5>Time Period: {group}</h5>
        <label>
            <input type="radio" bind:group value={'1999-2003'} class="my-super-special-classname">1999 - 2003
        </label>
        <label>
            <input type="radio" bind:group value={'2004-2008'} class="my-super-special-classname">2004 - 2008
        </label>
        <label>
            <input type="radio" bind:group value={'2009-2013'} class="my-super-special-classname">2009 - 2013
        </label>
        <label>
            <input type="radio" bind:group value={'2014-2018'} class="my-super-special-classname">2014 - 2018
        </label>
        <br />
        <br />
        <br />

        <label>
            <input type="color" bind:value={alc_color} onchange={changeAlcColor}>
            Alcohol Death Rate <br/> <span class="minimum_rate">{(alc_rate > 0) ? "(Minimum " + alc_rate + " per 100k ppl)" : ""}</span><br/>
            <input type="range" bind:value={alc_rate} min={-0.1} max={200} step={1} onchange={changeAlcRate}>
        </label>

        <label>
            <input type="color" bind:value={crh_color} onchange={changeCrhColor}>
            Cirrhosis Death Rate <br/> <span class="minimum_rate">{(crh_rate > 0) ? "(Minimum " + crh_rate + " per 100k ppl)" : ""}</span><br/>
            <input type="range" bind:value={crh_rate} min={-0.1} max={200} step={1} onchange={changeCrhRate}>
        </label>

        <label>
            <input type="color" bind:value={drg_color} onchange={changeDrgColor}>
            Drug Death Rate <br/> <span class="minimum_rate">{(drg_rate > 0) ? "(Minimum " + drg_rate + " per 100k ppl)" : ""}</span><br/>
            <input type="range" bind:value={drg_rate} min={-0.1} max={200} step={1} onchange={changeDrgRate}>
        </label>

        <label>
            <input type="color" bind:value={sui_color} onchange={changeSuiColor}>
            Suicide Death Rate <br/> <span class="minimum_rate">{(sui_rate > 0) ? "(Minimum " + sui_rate + " per 100k ppl)" : ""}</span><br/>
            <input type="range" bind:value={sui_rate} min={-0.1} max={200} step={1} onchange={changeSuiRate}>
        </label>

        <label>
            <input type="color" bind:value={dod_color} onchange={changeDodColor}>
            Deaths of Despair Rate <br/> <span class="minimum_rate">{(dod_rate > 0) ? "(Minimum " + dod_rate + " per 100k ppl)" : ""}</span><br/>
            <input type="range" bind:value={dod_rate} min={-0.1} max={200} step={1} onchange={changeDodRate}>
        </label>

        <footer class="js-footer"></footer>
    </div>
</aside>

<div id="loader">
    <div class="CDB-LoaderIcon CDB-LoaderIcon--big">
        <svg class="CDB-LoaderIcon-spinner" viewBox="0 0 50 50">
            <circle class="CDB-LoaderIcon-path" cx="25" cy="25" r="20" fill="none"></circle>
        </svg>
    </div>
</div>

<div class="charts controls right">
    <ChartPanel bind:values={chartData} bind:title={chartTitle} bind:subtitle={chartSubtitle} bind:bounds={boundsTitle} on:submit={handleFeatureSearch}/>
</div>

