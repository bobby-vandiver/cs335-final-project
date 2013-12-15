precision mediump float;

uniform sampler2D textureUnit;
uniform vec4 color;

varying vec2 interpolatedTextureCoordinates;

void main() {
    gl_FragColor = texture2D(textureUnit, interpolatedTextureCoordinates);
}