#version 330 core

in vec2 originalUV;

uniform sampler2D textureSampler;
uniform vec4 spriteRect;

out vec4 fragColor;

void main()
{
    vec2 spriteUV = spriteRect.xy + originalUV * spriteRect.zw;
    fragColor = texture(textureSampler, spriteUV);
}