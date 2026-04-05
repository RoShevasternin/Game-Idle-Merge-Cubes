#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_hue;        // 0.0 - 1.0
uniform float u_saturation; // 0.0 - 1.0
uniform float u_luminance;  // -1.0 - 1.0 (0 = без змін, >0 = світліше, <0 = темніше)

// ─── RGB → HSL ───────────────────────────────────────────────────────────────
vec3 rgb2hsl(vec3 c) {
    float maxC = max(c.r, max(c.g, c.b));
    float minC = min(c.r, min(c.g, c.b));
    float l    = (maxC + minC) / 2.0;
    float s    = 0.0;
    float h    = 0.0;
    float d    = maxC - minC;

    if (d > 0.001) {
        s = d / (1.0 - abs(2.0 * l - 1.0));

        if      (maxC == c.r) h = mod((c.g - c.b) / d, 6.0) / 6.0;
        else if (maxC == c.g) h = ((c.b - c.r) / d + 2.0) / 6.0;
        else                  h = ((c.r - c.g) / d + 4.0) / 6.0;
    }

    return vec3(h, s, l);
}

// ─── HSL → RGB ───────────────────────────────────────────────────────────────
vec3 hsl2rgb(vec3 hsl) {
    float h = hsl.x;
    float s = hsl.y;
    float l = hsl.z;

    float c = (1.0 - abs(2.0 * l - 1.0)) * s;
    float x = c * (1.0 - abs(mod(h * 6.0, 2.0) - 1.0));
    float m = l - c / 2.0;

    vec3 rgb;
    if      (h < 1.0 / 6.0) rgb = vec3(c, x, 0.0);
    else if (h < 2.0 / 6.0) rgb = vec3(x, c, 0.0);
    else if (h < 3.0 / 6.0) rgb = vec3(0.0, c, x);
    else if (h < 4.0 / 6.0) rgb = vec3(0.0, x, c);
    else if (h < 5.0 / 6.0) rgb = vec3(x, 0.0, c);
    else                     rgb = vec3(c, 0.0, x);

    return rgb + m;
}

// ─────────────────────────────────────────────────────────────────────────────

void main() {
    vec4 tex = texture2D(u_texture, v_texCoords);

    // Отримуємо HSL оригіналу
    vec3 hsl = rgb2hsl(tex.rgb);

    // Замінюємо hue і saturation, luminance оригіналу зберігаємо (+зміщення)
    float finalLuminance = clamp(hsl.z + u_luminance, 0.0, 1.0);

    vec3 recolored = hsl2rgb(vec3(
        fract(u_hue),       // hue — fract щоб 1.0 = 0.0 (кільце)
        u_saturation,
        finalLuminance      // luminance з оригіналу + зміщення
    ));

    gl_FragColor = vec4(recolored, tex.a) * v_color;
}
