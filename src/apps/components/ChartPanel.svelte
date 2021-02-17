<script>
	import { createEventDispatcher } from "svelte";
	import { onMount } from "svelte";
	import Chart from "./Chart.svelte";

	export let title = "";
	export let subtitle = "";
	export let bounds = "";
	export let values = [
		{ name: "data1" },
		{ name: "data2" },
		{ name: "data3" },
		{ name: "data4" },
		{ name: "data5" }
	];

	const dispatch = createEventDispatcher();
	onMount(() => {
		console.log(values);
	});

	const submit = () => dispatch("submit");
</script>

<style>
	.chart {
		grid-template-columns: repeat(3, 5em);
		grid-template-rows: repeat(4, 3em);
		grid-gap: 0.5em;
		padding: 4px 2px;
		text-shadow: none;
	}

	.chart h3,
	.chart h4,
	.chart h5 {
		margin: 6px 0px;
		text-shadow: none;
	}
</style>

<div class="chart">
	<h4>{title}</h4>
	<h5>{subtitle}<br />{bounds}</h5>

	{#each values as data}
		<Chart bind:data on:submit />
	{/each}
</div>