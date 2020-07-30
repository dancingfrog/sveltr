#!/usr/bin/env bash

cat content/post/data/terrain.mjs.1 content/post/data/planar-terrain-verts.json content/post/data/terrain.mjs.2 content/post/data/planar-terrain-norms.json content/post/data/terrain.mjs.3 content/post/data/planar-terrain-uvs.json content/post/data/terrain.mjs.4 content/post/data/planar-terrain-idx.json content/post/data/terrain.mjs.5 > src/apps/modules/terrain.mjs
