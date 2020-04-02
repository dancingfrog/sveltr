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

#define NAME cubemap-fragment-shader

uniform vec3 color;

#ifdef has_alpha
uniform float alpha;
#endif

uniform samplerCube uTexture;

in vec3 v_normal;

in vec3 v_view_position;

in vec2 v_textureCoords;

out mediump vec4 fragColor;

void main () {
	vec3 normal = normalize(v_normal);

	vec3 staticCameraPosition = vec3(0.5, 0.0, 0.1);
	vec3 eyeToSurfaceDir = normalize(staticCameraPosition - v_view_position);
	vec3 staticDirection = reflect(eyeToSurfaceDir, normal);

	fragColor = texture(uTexture, staticDirection); // TEXURE_CUBE_MAP
//	fragColor = texture(uTexture, v_textureCoords);
//	fragColor.rgb *= color;
}
