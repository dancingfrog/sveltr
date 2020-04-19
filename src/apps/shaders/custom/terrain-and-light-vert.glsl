/* The following builtins are prepended to
 * every custom vertex shader in @svelte/gl:
 */
/* start builtins */
//
//precision highp float;
//
//uniform mat4 MODEL;
//uniform mat4 PROJECTION;
//uniform mat4 VIEW;
//uniform mat4 MODEL_INVERSE_TRANSPOSE;
//
//uniform vec3 CAMERA_WORLD_POSITION;
//
//struct PointLight {
//	vec3 location;
//	vec3 color;
//	float intensity;
//// TODO fall-off etc
//};
//
//uniform PointLight POINT_LIGHTS[NUM_LIGHTS];
/* end builtins */

#define NAME terrain-vert

#define C_ZERO 0.0
#define C_ONE 1.0

// texture containing elevation data
//uniform sampler2D heightMap;
//uniform sampler2D bumpmap;
uniform sampler2D normalmap;

uniform float displace_multiply;

in vec3 position;

in vec3 normal;

in vec2 uv; // available when texture maps (bumpmap, colormap, ...) are used on object

out vec3 v_normal;

out vec2 v_textureCoords;

//struct direction_light {
	uniform vec3 light_direction; // normalized direction in eye
	uniform vec4 light_ambient_color;
	uniform vec4 light_diffuse_color;
	uniform vec4 light_specular_color;
//};

//struct material_props {
//	uniform vec4 material_ambient_color;
//	uniform vec4 material_diffuse_color;
//	uniform vec4 material_specular_color;
//	uniform float material_specular_exponent;
//};

//uniform material_props material;
//uniform direction_light light;

out vec4 v_shading;

vec4 directional_light_color (vec3 normal) {
	vec4 computed_color = vec4(C_ZERO, C_ZERO, C_ZERO, C_ZERO);
	vec3 nlight_direction = normalize(light_direction); // normalized direction in eye
	vec3 nlight_halfplane = normalize(vec3(nlight_direction.x + 0.0, nlight_direction.y + 1.0, nlight_direction.z + 0.0)); // normalized half-plane vector
	float ndotL; // dot product of normal & light direction
	float ndotH; // dot product of nomral and & half-plane vector

	ndotL = max(C_ZERO, dot(normal, nlight_direction));
	ndotH = max(C_ZERO, dot(normal, nlight_halfplane));
	computed_color += light_ambient_color * vec4(0.05, 0.05, 0.05, C_ONE); //material_ambient_color;
	computed_color += ndotL * light_diffuse_color * vec4(C_ONE, C_ONE, C_ONE, C_ONE); //material_diffuse_color;

//	if (ndotH > C_ZERO) {
//		computed_color += pow(ndotH, material_specular_exponent) * material_specular_color * light_specular_color;
//	}

	return computed_color;
}

void main() {
	float displacement = texture(normalmap, uv).b;

	vec3 displace_along_normal = vec3(normal * displacement);

	vec3 displaced_position = position + (displace_multiply * displace_along_normal);

	v_normal = normal + texture(normalmap, uv).rgb;

	v_textureCoords = uv;

	v_shading = directional_light_color((MODEL_INVERSE_TRANSPOSE * vec4(v_normal, C_ZERO)).xyz);

	gl_Position = PROJECTION * VIEW * MODEL * vec4(displaced_position, C_ONE);
}
