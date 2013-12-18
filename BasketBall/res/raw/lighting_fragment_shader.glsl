precision mediump float;

uniform vec3 lightPosition;

uniform vec4 diffuseColor;
uniform float diffuseIntensity;

uniform vec4 specularColor;
uniform float specularIntensity;
uniform float shininess;

uniform vec4 color;

varying vec3 surfaceNormal;
varying vec3 interpolatedPosition;

void main() {

    vec3 surfaceUnitNormal = normalize(surfaceNormal);
    vec3 incidentLight = normalize(lightPosition - interpolatedPosition);

    vec4 diffuse = diffuseIntensity * diffuseColor * max(dot(surfaceUnitNormal, incidentLight), 0.0);

    vec3 reflectedLight = reflect(surfaceUnitNormal, incidentLight);
    vec3 viewUnitDirection = normalize(interpolatedPosition);

    float reflectedDotView = max(dot(reflectedLight, viewUnitDirection), 0.0);

    vec4 specular = specularColor * specularIntensity * pow(reflectedDotView, shininess);

    gl_FragColor = color * (diffuse + specular);
}