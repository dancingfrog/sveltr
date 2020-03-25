attribute vec3 position;
attribute vec3 normal;

varying vec3 v_normal;

void main() {
	vec4 pos = vec4(position, 1.0);
	vec4 model_view_pos = VIEW * MODEL * pos;

	v_normal = (MODEL_INVERSE_TRANSPOSE * vec4(normal, 0.0)).xyz;

	gl_Position = PROJECTION * model_view_pos;
}
