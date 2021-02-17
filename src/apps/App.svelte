<script>
  import { onMount } from 'svelte';
  import Keypad from './components/Keypad.svelte';
  import carto from '@carto/carto-vl';
  import mapboxgl from 'mapbox-gl';

  let map;

  export let color = '#cb1c36';
  let w = 1;
  let h = 1;
  let dod = 0;
  let group = '1999-2003';
  let selection = [];

  let changeDodColor = (color) => { console.log("Set color for Deaths of Despair ", color)};
  $: changeDodColor(color);

  let changeDodRate = (dod) => { console.log("Set minimum rate for Deaths of Despair ", dod)};
  $: changeDodRate(dod);

  let changeTimePeriod = (group) => { console.log("Set time period of dataset", group)};
  $: changeTimePeriod(group);

  onMount(() => {
    let frame;

    console.log(carto.basemaps.darkmatter);
    const map = new mapboxgl.Map({
      container: 'map',
      style: carto.basemaps.darkmatter,
      center: [-96, 30],
      zoom: 7,
      scrollZoom: true
    });

    const nav = new mapboxgl.NavigationControl({
      showCompass: false
    });
    map.addControl(nav, 'top-left');
    map.addControl(new mapboxgl.FullscreenControl(), 'top-left');

    // Define user
    carto.setDefaultAuth({
      username: 'cartovl',
      apiKey: 'default_public'
    });

    let layerA;
    let layerB;
    let currentLayer = 'A'; // 'B'
    let data_source;

    let init = false;

    let sinceLastRateChange = 0;

    const fetchNewData = (dod_color, dod_rate, time_period) => {

      const style_ramp = `ramp(linear($crude_rate,1,20), [gold,  ${dod_color}])`;

      if (((new Date()).getTime() - sinceLastRateChange) < 533) {
        // Only allow updates to rate param every half-second or so
        return;
      } else {
        sinceLastRateChange = (new Date()).getTime();
      }

      if (!!init) {
        const viz2 = new carto.Viz(`
          @style: opacity(${style_ramp}, 0.05)
          color: @style
          width: 1
        `);

        if(currentLayer === 'A' && !!layerA && layerA.hasOwnProperty("id")) {
          // Fade out
          layerA.viz.color.blendTo(`opacity(${style_ramp}, 0.05)`)
          setTimeout(() => {
            console.log("Removing layer A");
            layerA.remove();
          }, 533);
          currentLayer = 'B';

        } else if (!!layerB && layerB.hasOwnProperty("id")) {
          layerA.viz.color.blendTo(`opacity(${style_ramp}, 0.05)`)
          setTimeout(() => {
            console.log("Removing layer B");
            layerB.remove();
          }, 533);
          currentLayer = 'A';
        }
      }

      // const sql_query =  `select * from "ruralinnovation-admin".dod_covid_county where death_cause ilike 'DoD'`,
      const sql_query = `select cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,time_interval,time_period,crude_rate,death_cause,age_group,gender,race,population,deaths_dod,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,the_geom_webmercator from "ruralinnovation-admin".dod_covid_county where death_cause ilike 'DoD' and time_period ilike '${time_period}' and crude_rate > ${dod_rate}`;
      console.log(sql_query);

      const viz1 = new carto.Viz(`
		  @style: opacity(${style_ramp}, 0.05)
		  color: @style
		  strokeColor: @style
		`);
      // filter: animation(linear($crude_rate,144.5, 1.5),5,fade(0,100))+0.1

      try {
        if (!!init && !!layerA && layerA.hasOwnProperty("id")) {
          map.removeLayer(currentLayer); // immediately clear next layer
          // map.removeSource(currentLayer);
        }
      } finally {

        console.log("Adding layer ", currentLayer);

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

        if (currentLayer === 'A') {
          layerA = new carto.Layer(currentLayer, dod_covid_county_source, viz1);
          layerA.addTo(map);
          window.layer = layerA;

          layerA.on('loaded', async () => {
            document.getElementById('loader').style.opacity = '0';
            setTimeout(() => {
              // Fade In
              layerA.viz.color.blendTo(`opacity(${style_ramp}, 0.75)`);
              init = true;
            }, 333);
          });

        } else if (currentLayer === 'B') {
          layerB = new carto.Layer(currentLayer, dod_covid_county_source, viz1);
          layerB.addTo(map);
          window.layer = layerB;

          layerB.on('loaded', async () => {
            document.getElementById('loader').style.opacity = '0';
            setTimeout(() => {
              // Fade In
              layerB.viz.color.blendTo(`opacity(${style_ramp}, 0.75)`);
              init = true;
            }, 333);
          });
        }
      }

    }

    fetchNewData(color, dod, group);

    changeDodColor = (dod_color) => {
      fetchNewData(dod_color, dod, group)
    };

    changeDodRate = (dod_rate) => {
      fetchNewData(color, dod_rate, group)
    };

    changeTimePeriod = (time_period) => {
      fetchNewData(color, dod, time_period)
    };

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
    bottom:24px;
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
</style>

<div id="map"></div>
<aside class="controls right toolbox">
    <div class="box">
        <header>
            <h1>Deaths of Despair: Covid-19 and Historic Mortality Rates</h1>
        </header>
        <section>
            <p class="description open-sans">This dataset allows one to explore
                potential correlations between historic mortality factors/rates and the
                total number of deaths due to Covid-19 (in 2020), and to “drill down” on
                those relationships within a given location (county or metropolitan
                area)</p>
        </section>
        <hr />

        <h3>Time Period: {group}</h3>
        <label>
            <input type="radio" bind:group value={'1999-2003'} class="my-super-special-classname">
            1999 - 2003
        </label>
        <label>
            <input type="radio" bind:group value={'2004-2008'} class="my-super-special-classname">
            2004 - 2008
        </label>
        <label>
            <input type="radio" bind:group value={'2009-2013'} class="my-super-special-classname">
            2009 - 2013
        </label>
        <label>
            <input type="radio" bind:group value={'2014-2018'} class="my-super-special-classname">
            2014 - 2018
        </label>

        <label>
            <input type="color" style="height: 40px" bind:value={color} onchange={changeDodColor}>
        </label>
        <label>
            Deaths of Despair Rate <br /> {(dod > 0) ? "(Minimum "+ dod + " per 100k ppl)" : ""}<br />
            <input type="range" bind:value={dod} min={0.1} max={100} step={1} onchange={changeDodRate}>
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
