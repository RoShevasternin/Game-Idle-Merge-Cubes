#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec2 v_localUV;   // 0..1 в межах регіону (від BATCH_VERT)
// y=0 верх, y=1 низ (SpriteBatch конвенція)

uniform sampler2D u_texture;
uniform float u_fillPercent;

void main() {
    vec4  tex  = texture2D(u_texture, v_texCoords);
    float gray = dot(tex.rgb, vec3(0.299, 0.587, 0.114));
    float fill = u_fillPercent / 100.0;

    // v_localUV.y: 0 = верх, 1 = низ (SpriteBatch: нижні вершини мають більше v)
    // Заповнення знизу вгору:
    //   fill=0.0 → step(1.0, y) → кольорові де y >= 1.0 → нічого ✓
    //   fill=0.5 → step(0.5, y) → кольорові де y >= 0.5 → нижня половина ✓
    //   fill=1.0 → step(0.0, y) → кольорові де y >= 0.0 → все ✓
    float mask = step(1.0 - fill, v_localUV.y);

    vec3 color = mix(vec3(gray), tex.rgb, mask);
    gl_FragColor = vec4(color, tex.a) * v_color;
}
