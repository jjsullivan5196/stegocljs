with import <nixpkgs> {};

stdenv.mkDerivation {
  name = "stegocljs";
  src = ./.;
  buildInputs = [
    nodejs
    yarn
    adoptopenjdk-openj9-bin-11
  ];
}
