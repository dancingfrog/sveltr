/* The following builtins are prepended to
 * every custom fragment shader in @svelte/gl:
 */
/* start builtins */
//
//precision highp float;
//
//struct DirectionalLight {
//	vec3 direction;
//	vec3 color;
//	float intensity;
//};
//
//struct PointLight {
//	vec3 location;
//	vec3 color;
//	float intensity;
//// TODO fall-off etc
//};
//
//uniform vec3 AMBIENT_LIGHT;
//uniform DirectionalLight DIRECTIONAL_LIGHTS[NUM_LIGHTS];
//uniform PointLight POINT_LIGHTS[NUM_LIGHTS];
/* end builtins */

uniform float specularity;

uniform vec3 emissive;

#ifdef has_colormap
uniform sampler2D colormap;
in vec2 v_uv;
#endif

#ifdef has_alpha
uniform float alpha;
#endif

in vec3 v_surface_to_light[NUM_LIGHTS];
in vec3 v_surface_to_view[NUM_LIGHTS];

uniform vec3 color;

in vec3 v_normal;

out mediump vec4 fragColor;

void main () {
	vec3 normal = normalize(v_normal);

	vec3 lighting = vec3(0.0);
	vec3 spec_amount = vec3(0.0);

	// directional lights
	for (int i = 0; i < NUM_LIGHTS; i += 1) {
		DirectionalLight light = DIRECTIONAL_LIGHTS[i];

		float multiplier = clamp(dot(normal, -light.direction), 0.0, 1.0);
		lighting += multiplier * light.color * light.intensity;
	}

	// point lights
	for (int i = 0; i < NUM_LIGHTS; i += 1) {
		PointLight light = POINT_LIGHTS[i];

		vec3 surface_to_light = normalize(v_surface_to_light[i]);

		float multiplier = clamp(dot(normal, surface_to_light), 0.0, 1.0); // TODO is clamp necessary?
		lighting += multiplier * light.color * light.intensity;

		vec3 surface_to_view = normalize(v_surface_to_view[i]);
		vec3 half_vector = normalize(surface_to_light + surface_to_view);
		float spec = clamp(dot(normal, half_vector), 0.0, 1.0);

		spec_amount += specularity * spec * light.color * light.intensity;
	}

	fragColor = vec4(color, 1.0);

//	#ifdef has_colormap
//	vec4 textureColor = texture(colormap, v_uv);
//	fragColor.r = (0.55 * fragColor.r) + (0.45 * textureColor.r);
//	fragColor.g = (0.55 * fragColor.g) + (0.45 * textureColor.g);
//	fragColor.b = (0.55 * fragColor.b) + (0.45 * textureColor.b);
//	#endif

//	fragColor.rgb *= mix(AMBIENT_LIGHT, vec3(1.0, 1.0, 1.0), lighting);
//	fragColor.rgb += spec_amount;
//
//	fragColor.rgb += emissive;

	#ifdef has_alpha
	fragColor.a *= alpha;
	#endif
}
