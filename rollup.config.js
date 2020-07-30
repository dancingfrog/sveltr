import svelte from 'rollup-plugin-svelte';
import commonjs from 'rollup-plugin-commonjs';
import copy from 'rollup-plugin-copy'
import resolve from 'rollup-plugin-node-resolve';
import postcss from "rollup-plugin-postcss";
import shader from 'rollup-plugin-shader';

export default {
	input: 'src/main.js',
	output: {
		sourcemap: true,
		format: 'iife',
		name: 'app',
		exports: 'named',
		file: 'public/main.js'
	},
	plugins: [

		commonjs(),

		copy({
			targets: [
				{ src: 'src/images', dest: 'public/' },
				{ src: 'static/*', dest: 'public/' }
			]
		}),

		postcss({
			extract: 'public/global.css',
			plugins: []
		}),

		// If you have external dependencies installed from
		// npm, you'll most likely need these plugins. In
		// some cases you'll need additional configuration -
		// consult the documentation for details:
		// https://github.com/rollup/plugins/tree/master/packages/commonjs
		resolve({
			browser: true,
			dedupe: ['svelte']
		}),

		shader({
			// All match files will be parsed by default,
			// but you can also specifically include/exclude files
			include: [
				'../@sveltejs/gl/**/*.glsl',
				'**/*.glsl',
				'**/*.vs',
				'**/*.fs' ],
			// specify whether to remove comments
			removeComments: true,   // default: true
		}),

		svelte({
			// enable run-time checks when not in production
			dev: false,
			// we'll extract any component CSS out into
			// a separate file - better for performance
			css: css => {
				css.write('public/main.css');
			}
		})
	],
	watch: {
		clearScreen: false
	}
};

function serve() {
	let started = false;

	return {
		writeBundle() {
			if (!started) {
				started = true;

				require('child_process').spawn('npm', ['run', 'start', '--', '--dev'], {
					stdio: ['ignore', 'inherit', 'inherit'],
					shell: true
				});
			}
		}
	};
}
