<script>
	import { createEventDispatcher } from 'svelte';

	export let value = [
		{ name: 'data1'},
		{ name: 'data2'},
		{ name: 'data3'},
		{ name: 'data4'},
		{ name: 'data5'}
	];

	const dispatch = createEventDispatcher();

	const select = num => () => value += num;
	const clear  = () => value = '';
	const submit = () => dispatch('submit');
</script>

<style>
	.chart {
		display: grid;
		grid-template-columns: repeat(3, 5em);
		grid-template-rows: repeat(4, 3em);
		grid-gap: 0.5em
	}

	button {
		margin: 0
	}
</style>

<div class="chart">
	{#each value as data}
		<div>&nbsp;{data['name']}&nbsp;</div>
	{/each}
<!--	<button disabled={!value} on:click={submit}>submit</button>-->
</div>