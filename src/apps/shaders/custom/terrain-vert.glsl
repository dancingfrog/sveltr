in vec3 position;
in vec3 normal;

out vec3 v_normal;

#if defined(has_colormap) || defined(has_specularitymap) || defined(has_normalmap) || defined(has_bumpmap)
#define has_textures true
#endif

#ifdef has_textures
in vec2 uv;
out vec2 v_uv;
#endif

#if defined(has_normalmap) || defined(has_bumpmap)
out vec3 v_view_position;
#endif

out vec3 v_surface_to_light[NUM_LIGHTS];
out vec3 v_surface_to_view[NUM_LIGHTS];

#ifdef USE_FOG
out float v_fog_depth;
#endif

#define NAME terrain-vert

// texture containing elevation data
uniform sampler2D heightMap;

void main() {
	float displacement = texture(heightMap, uv).r;

	vec3 displace_along_normal = vec3(normal * displacement);

	vec3 displaced_position = position + (0.99 * displace_along_normal);

	//	vec4 pos = vec4(position, 1.0);
	vec4 pos = vec4(displaced_position, 1.0);
	vec4 model_view_pos = VIEW * MODEL * pos;

	mat4 mvp_matrix = PROJECTION * VIEW * MODEL;

	// NEED TO CALCULATE NORMAL DISPLACEMENT BY SAMPLING NEIGHBOR UV's
//	v_normal = (MODEL_INVERSE_TRANSPOSE * vec4(normal + displacement, 0.0)).xyz;
	v_normal = normal + displacement;

	#ifdef has_textures
	v_uv = uv;
	#endif

	#if defined(has_normalmap) || defined(has_bumpmap)
	v_view_position = model_view_pos.xyz;
	#endif

	#ifdef USE_FOG
	v_fog_depth = -model_view_pos.z;
	#endif

	for (int i = 0; i < NUM_LIGHTS; i += 1) {
		PointLight light = POINT_LIGHTS[i];

		vec3 surface_world_position = (MODEL * pos).xyz;
		v_surface_to_light[i] = light.location - surface_world_position;

		#ifdef has_specularity
		v_surface_to_view[i] = CAMERA_WORLD_POSITION - surface_world_position;
		#endif
	}

//	gl_Position = PROJECTION * VIEW * MODEL * pos;
//	gl_Position = PROJECTION * model_view_pos;
	gl_Position = mvp_matrix * pos;
}
