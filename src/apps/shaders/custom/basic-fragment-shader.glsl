/* The following builtins are prepended to
 * every custom fragment shader in @svelte/gl:
 */
/* start builtins */
//#extension GL_OES_standard_derivatives : enable

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

uniform vec3 color;

#ifdef has_alpha
uniform float alpha;
#endif

in vec3 v_normal;

out mediump vec4 fragColor;

void main () {
	vec3 normal = normalize(v_normal);

	fragColor = vec4(color, 1.0);

	#ifdef has_alpha
	fragColor.a *= alpha;
	#endif
}