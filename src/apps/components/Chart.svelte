<script>
	import { createEventDispatcher } from 'svelte';
	import { onMount, beforeUpdate } from 'svelte';
	import { extent,  min, max, scaleLinear, range } from 'd3';

	export let data = { name: 'data1'};

	const dispatch = createEventDispatcher();

	let svg;
	let width = 320;
	let height = 240;
	let domainX = [];
	let domainY = [];
	const padding = { top: 20, right: 40, bottom: 20, left: 25 };

	let xScale = scaleLinear()
		.domain(domainX)
		.range([padding.left, width - padding.right]);
	let yScale = scaleLinear()
		.domain(domainY)
		.range([height - padding.bottom, padding.top]);
	let xTicks = [];
	let yTicks = [];

	let mean_line = 0;

	const resize = () => ({ width, height } = (!!svg && typeof svg['getBoundingClientRect'] === 'function')? svg.getBoundingClientRect() : { width, height });

	const submit = () => dispatch('submit');

	beforeUpdate(() => {
		// alert("Update chart " + data['name']);

		const mean = data['aggregations']['avg_grade']['value'];
		console.log(mean_line);

		const extentX = extent(data['points'], d => {
			return d['x'];
		});
		const maxX = max(data['points'], d => {
			return d['x'];
		});
		const minX = min(data['points'], d => {
			return d['x'];
		});
		domainX = extentX;
		// console.log("Extent: ", extentX);
		// console.log("Min: ", minX);
		// console.log("Max: ", maxX);
		xScale = scaleLinear()
			.domain(domainX)
			.range([ 0, width - padding.right]);

		xTicks = (maxX - minX) > 100 ?
			range(0, maxX + (maxX - minX) / 5, 20) :
			range(0, maxX + (maxX - minX) / 5, 5);

		// console.log("Range: ", xTicks);

		const extentY = extent(data['points'], d => {
			return d['y'];
		});
		const maxY = max(data['points'], d => {
			return d['y'];
		});
		const minY = min(data['points'], d => {
			return d['y'];
		});
		domainY = extentY;
		// console.log("Extent: ", extentY);
		// console.log("Min: ", minY);
		// console.log("Max: ", maxY);

		yScale = scaleLinear()
			.domain(domainY)
			.range([ height - (padding.bottom), 0 ]);

		yTicks = height > 200 ?
			range(0, maxY + (maxX - minY) / 5, 50) :
			range(0, maxY + (maxX - minY) / 5, 100);

		// console.log("Range: ", yTicks);

		mean_line = yScale(mean);
	});

	onMount(() => {
		resize();
	});
</script>

<style>
	.chart {
		text-shadow: none;
		color: #000;
	}

	.chart * {
		text-shadow: none;
	}

	svg {
		width: 100%;
		height: 100%;
		float: left;
	}

	circle {
		fill: orange;
		fill-opacity: 0.6;
		stroke: rgba(0,0,0,0.5);
	}

	circle:hover {
		fill-opacity: 0.75;
		stroke: #FFF;
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

{#if (!!xTicks && xTicks.length > 0 && !!yTicks && yTicks.length > 0) }
<div style="min-width: {width}px; min-height: {height}px; overflow-x: hidden; margin-top: 8px; text-decoration: underline;">
	&nbsp;{data['name']}&nbsp;
	<svg bind:this={svg} style="min-width: {width}px; min-height: {height}px; width: 100%;">
		<!-- y axis -->
		<g transform="translate({padding.left}, 0)">
			{#each yTicks as y}
				<g class="tick" opacity="1" transform="translate(0,{yScale(y)})">
					<line stroke="currentColor" x2="-5" />
					<text dy="0.32em" fill="currentColor" x="-{padding.left}">
						{y}
					</text>
				</g>
			{/each}
		</g>
		<text x="{-height}" y="{-width}" style="text-shadow: none; font-weight: bolder;color: black;" transform="rotate(90)">
			Covid-19 2020 Death Rate (per 100k)</text>

		<!-- x axis -->
		<g class='axis x-axis' transform="translate(0, 0)">
			{#each xTicks as tick}
				<g class='tick' transform='translate({xScale(tick) + padding.left},0)'>
					<line y1='{yScale(0)}' y2='{yScale(50)}'/>
					<text y='{height - padding.bottom + 16}'>{tick}</text>
				</g>
			{/each}
		</g>

		<!-- data -->
		<g class='graph-data'  transform='translate({padding.left},0)'>
			<line x1="{xScale(0)}" y1="{mean_line}" x2="{xScale(max(data['points'], d => d['x']))}" y2="{mean_line - 4}" style="stroke:red" />
			<text x="{width / 2 + 2 * padding.left}" y="{mean_line - (4 + padding.bottom)}" style="text-shadow: none; font-weight: bolder;color: black;" text-anchor="middle">
				Covid 19 Mean Avg Death</text>
			<text x="{width / 2 + 2 * padding.left}" y="{mean_line - 6}" style="text-shadow: none; font-weight: bolder;color: black;" text-anchor="middle">
				Rate For This Sample</text>
		{#each data['points'] as point}
			<circle cx='{xScale(point.x)}' cy='{yScale(point.y)}' r='5' on:click="{(evt) => submit(evt)}" data="{JSON.stringify(point)}" />
		{/each}
		</g>
	</svg>
</div>
{/if}

