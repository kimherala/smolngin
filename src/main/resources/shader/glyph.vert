#version 330 core

layout (location = 0) in vec4 position;
out vec2 TextCoords;

uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * vec4(position.xy, 0.0, 1.0);
    TextCoords = position.zw;
}