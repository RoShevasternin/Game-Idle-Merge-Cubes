#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;

uniform float u_time;
uniform float u_edgeDeform;
uniform float u_finishFlash;

float hash(vec2 p)
{
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

void main()
{
    vec2 uv = v_texCoords;
    float t = u_time;

    float w1 = sin((uv.x*6.0 + uv.y*4.0) + t*3.0);
    float w2 = cos((uv.x*5.0 - uv.y*7.0) + t*2.4);
    float w3 = sin((uv.x*9.0 + uv.y*3.0) - t*2.8);

    vec2 distortion = vec2(w1 + w2, w2 + w3) * 0.006 * u_edgeDeform;

    vec4 tex = texture2D(u_texture, uv + distortion);

    // sparkle
    vec2 cell = floor(uv * 100.0);
    float sparkle = hash(cell + floor(t*6.0));
    sparkle = smoothstep(0.985, 1.0, sparkle);

    vec3 color = tex.rgb + sparkle * 0.4;

    // finish flash (тільки де є текстура)
    color += u_finishFlash * 0.8 * tex.a;

    gl_FragColor = vec4(color, tex.a) * v_color;
}