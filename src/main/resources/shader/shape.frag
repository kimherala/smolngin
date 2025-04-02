#version 330

out vec4 color;

uniform vec3 shapeColor;

void main() {
    color = vec4(shapeColor, 1.0);
}