#version 150

uniform sampler2D DepthSampler;

uniform mat4 InverseWorldProjMat;
uniform vec4 DepthParams;
uniform vec4 FogRange;
uniform vec4 FogStrength;
uniform vec4 FogColor;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float smoothBand(float from, float to, float value) {
    float width = max(0.0001, to - from);
    float progress = clamp((value - from) / width, 0.0, 1.0);
    return progress * progress * (3.0 - 2.0 * progress);
}

float linearizeDepth(float depth) {
    float nearPlane = DepthParams.x;
    float farPlane = DepthParams.y;
    float z = depth * 2.0 - 1.0;
    return (2.0 * nearPlane * farPlane) / max(farPlane + nearPlane - z * (farPlane - nearPlane), 0.0001);
}

float reconstructViewDistance(vec2 uv, float depth) {
    vec4 clipPos = vec4(uv * 2.0 - 1.0, 1.0, 1.0);
    vec4 viewPos = InverseWorldProjMat * clipPos;
    vec3 viewDirection = normalize(viewPos.xyz / max(viewPos.w, 0.0001));
    return linearizeDepth(depth) * length(viewDirection);
}

void main() {
    float rawDepth = texture(DepthSampler, texCoord0).r;

    if (rawDepth >= 0.999999) {
        fragColor = vec4(FogColor.rgb, FogStrength.z) * vertexColor;
        return;
    }

    float radialDistance = reconstructViewDistance(texCoord0, rawDepth);
    float fogAmount;

    if (radialDistance <= FogRange.x) {
        fogAmount = 0.0;
    } else if (radialDistance < FogRange.y) {
        fogAmount = mix(0.0, FogStrength.x, smoothBand(FogRange.x, FogRange.y, radialDistance));
    } else if (radialDistance < FogRange.z) {
        fogAmount = mix(FogStrength.x, FogStrength.y, smoothBand(FogRange.y, FogRange.z, radialDistance));
    } else {
        fogAmount = mix(FogStrength.y, FogStrength.z, smoothBand(FogRange.z, FogRange.w, radialDistance));
    }

    fragColor = vec4(FogColor.rgb, clamp(fogAmount, 0.0, 1.0)) * vertexColor;
}
