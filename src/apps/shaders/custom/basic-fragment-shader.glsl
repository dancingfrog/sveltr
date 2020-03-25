/* This builtin is prepended to every
 * custom vertext shader in Svelte:
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

varying vec3 v_normal;

void main () {
	vec3 normal = normalize(v_normal);

	gl_FragColor = vec4(color, 1.0);
}
