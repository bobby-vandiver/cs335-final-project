precision mediump float;

uniform vec3 lightPosition;

uniform sampler2D textureUnit;

varying vec3 surfaceNormal;

varying vec3 interpolatedPosition;
varying vec2 interpolatedTextureCoordinates;

void main() {

    vec3 surfaceUnitNormal = normalize(surfaceNormal);
    vec3 incidentLight = normalize(lightPosition - interpolatedPosition);

    vec4 diffuseColor = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 diffuse = diffuseColor * max(dot(surfaceUnitNormal, incidentLight), 0.0);

    vec4 textureColor = texture2D(textureUnit, interpolatedTextureCoordinates);

    gl_FragColor = textureColor * diffuse;
}