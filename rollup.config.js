import browsersync from 'rollup-plugin-browsersync';
import commonjs from 'rollup-plugin-commonjs';
import copy from 'rollup-plugin-copy';
import dist from 'rollup-plugin-copy2';
import del from 'rollup-plugin-delete';
import nested from 'postcss-nested';
import postcss from 'rollup-plugin-postcss';
import resolve from 'rollup-plugin-node-resolve';
import { terser } from 'rollup-plugin-terser';
// import zip from 'rollup-plugin-zip';

const production = !process.env.ROLLUP_WATCH;

export default [

	{
		input: 'src/main.scss',
		output: {
			file: 'public/main.css',
			format: 'es',

			sourcemap: true,
		},
		plugins: [
			commonjs(),

			del({
				targets: [
					'public/data',
					'public/fonts',
					'public/imports'
				],
				verbose: true
			}),

			copy({
				targets: [
					{ src: 'src/data', dest: 'public/' },
					{ src: 'src/fonts/*.eot', dest: 'public/fonts/' },
					{ src: 'src/fonts/*.svg', dest: 'public/fonts/' },
					{ src: 'src/fonts/*.ttf', dest: 'public/fonts/' },
					{ src: 'src/fonts/*.woff', dest: 'public/fonts/' },
					{ src: 'src/fonts/*.woff2', dest: 'public/fonts/' },
					{ src: 'src/images', dest: 'public/' },
					{ src: 'src/styles/imports', dest: 'public/' },
					{ src: 'static/*', dest: 'public/' }
				]
			}),

			postcss({
				modules: true,
				extract: true,
				// extract: 'public/global.css',
				plugins: [
					// cssnano(),
					// cssnext({ warnForDuplicates: false, }),
					nested()
				],
			}),

			// Watch the `public` directory and refresh the
			// browser on changes when not in production
			!production && browsersync({ server: 'dist', startPath: '/InProcess/ReleaseStatistics/' }),

			// In dev mode, call `npm run start` once
			// the bundle has been generated
			// !production && serve('public'),

			// If we're building for production (npm run build
			// instead of npm run dev), minify
			production && terser()
		],
		watch: {
			clearScreen: false
		}
	},
	{
		input: 'src/lambda/main.js',
		output: {
			dir: 'dist/lambda',
			format: 'commonjs', //'umd',
			name: 'self',
			extend: true,
			export: 'named',
		},
		plugins: [
			commonjs(),
			dist({
				assets: [
					['src/lambda/index.js', 'index.js'],
				]
			}),
			resolve({
				browser: false
			}),
			// zip({
			// 	dir: 'dist'
			// })
		],
		watch: {
			clearScreen: false
		}
	}
];
