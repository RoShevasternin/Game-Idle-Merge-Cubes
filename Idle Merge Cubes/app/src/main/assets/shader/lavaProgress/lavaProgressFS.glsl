#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec2 v_localUV;      // 0..1 в межах спрайту (від BATCH_VERT)

uniform sampler2D u_texture;
uniform float     u_time;
uniform float     u_edgeDeform;
uniform float     u_finishFlash;

uniform vec2 u_uvMin;
uniform vec2 u_uvMax;

float hash(vec2 p)
{
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

void main()
{
    float t = u_time;

    // Хвилі рахуємо у v_localUV (0..1) — незалежно від позиції в атласі.
    // Якби рахували у v_texCoords (0.3..0.6), хвильовий патерн був би
    // "вирізаний" з середини синусоїди → весь спрайт рухається як ціле.
    float w1 = sin((v_localUV.x * 6.0 + v_localUV.y * 4.0) + t * 3.0);
    float w2 = cos((v_localUV.x * 5.0 - v_localUV.y * 7.0) + t * 2.4);
    float w3 = sin((v_localUV.x * 9.0 + v_localUV.y * 3.0) - t * 2.8);

    // Offset у локальному просторі (0..1)
    vec2 distortionLocal = vec2(w1 + w2, w2 + w3) * 0.006 * u_edgeDeform;

    // Масштабуємо offset в атласний UV простір.
    // distortionAtlas = distortionLocal * uvRange
    // Без цього offset 0.006 може вилізти за межі регіону і показати сусідній спрайт.
    vec2 uvRange         = u_uvMax - u_uvMin;
    vec2 distortionAtlas = distortionLocal * uvRange;

    vec4 tex = texture2D(u_texture, v_texCoords + distortionAtlas);

    // Sparkle grid у v_localUV — рівномірно по всьому спрайту
    vec2  cell    = floor(v_localUV * 100.0);
    float sparkle = hash(cell + floor(t * 6.0));
    sparkle = smoothstep(0.985, 1.0, sparkle);

    vec3 color = tex.rgb + sparkle * 0.4;
    color += u_finishFlash * 0.8 * tex.a;

    gl_FragColor = vec4(color, tex.a) * v_color;
}
