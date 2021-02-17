<script>
	import { createEventDispatcher } from 'svelte';
	import { onMount } from 'svelte';
	import { scaleLinear } from 'd3-scale';

	export let value = [
		{ name: 'data1'},
		{ name: 'data2'},
		{ name: 'data3'},
		{ name: 'data4'},
		{ name: 'data5'}
	];

	const dispatch = createEventDispatcher();

	let svg;
	let width = 500;
	let height = 200;
	const padding = { top: 20, right: 40, bottom: 40, left: 25 };
	$: xScale = scaleLinear()
		.domain([0, 20])
		.range([padding.left, width - padding.right]);
	$: yScale = scaleLinear()
		.domain([0, 12])
		.range([height - padding.bottom, padding.top]);
	$: xTicks = width > 180 ?
		[0, 4, 8, 12, 16, 20] :
		[0, 10, 20];
	$: yTicks = height > 180 ?
		[0, 2, 4, 6, 8, 10, 12] :
		[0, 4, 8, 12];

	function resize() {
		// ({ width, height } = svg.getBoundingClientRect());
	}

	onMount(resize);

	const select = num => () => value += num;
	const clear  = () => value = '';
	const submit = () => dispatch('submit');
</script>

<style>
	.chart {
		grid-template-columns: repeat(3, 5em);
		grid-template-rows: repeat(4, 3em);
		grid-gap: 0.5em;
		padding: 4px 2px;
	}

	button {
		margin: 0
	}

	svg {
		width: 50%;
		height: 50%;
		float: left;
	}
	circle {
		fill: orange;
		fill-opacity: 0.6;
		stroke: rgba(0,0,0,0.5);
	}
	.tick line {
		stroke: #ddd;
		stroke-dasharray: 2;
	}
	text {
		font-size: 12px;
		fill: #999;
	}
	.x-axis text {
		text-anchor: middle;
	}
	.y-axis text {
		text-anchor: end;
	}
</style>

<svelte:window on:resize='{resize}'/>

<div class="chart">

	{#each value as data}
		<div style="min-width: 264px; min-height: 200px; overflow-x: hidden;">&nbsp;{data['name']}&nbsp;
			<svg bind:this={svg} style="min-width: 2640px; min-height: 200px; width: 100%;">
				<!-- y axis -->
				<g class='axis y-axis'>
					{#each yTicks as tick}
						<g class='tick tick-{tick}' transform='translate(0, {yScale(tick)})'>
							<line x1='{padding.left}' x2='{xScale(22)}'/>
							<text x='{padding.left - 8}' y='+4'>{tick}</text>
						</g>
					{/each}
				</g>

				<!-- x axis -->
				<g class='axis x-axis'>
					{#each xTicks as tick}
						<g class='tick' transform='translate({xScale(tick)},0)'>
							<line y1='{yScale(0)}' y2='{yScale(13)}'/>
							<text y='{height - padding.bottom + 16}'>{tick}</text>
						</g>
					{/each}
				</g>

				<!-- data -->
				{#each data['points'] as point}
					<circle cx='{xScale(point.x)}' cy='{yScale(point.y)}' r='5' on:click="{(evt) => submit(evt)}" data="{JSON.stringify(point)}" />
				{/each}
			</svg>
		</div>
	{/each}
</div>