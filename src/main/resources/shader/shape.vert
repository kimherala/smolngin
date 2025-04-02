#version 330

layout (location = 0) in vec4 position;

uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * vec4(position.xy, 0.0, 1.0);
}