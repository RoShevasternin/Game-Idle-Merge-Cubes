#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;    // Основна текстура

void main() {
    vec4 tex = texture2D(u_texture, v_texCoords);
    tex.rgb *= tex.a; // premultiply
    gl_FragColor = tex * v_color;

    //gl_FragColor = vec4(v_color.rgb * v_color.a, v_color.a) * texture2D(u_texture, v_texCoords);
}
