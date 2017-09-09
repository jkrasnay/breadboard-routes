# Breadboard Routing Utility

This is a tool to help routing wires on breadboards. I created it to help with
the [8-bit computer](https://eater.net/) I'm building with my kids.

Each board in the project is specified in its own EDN file. The file specifies
where the chips are placed on the board and which jumpers are required in terms
of chip and pin numbers. The routing tool does the calculations to say which
breadboard rows to connect and the lengths of the required jumpers.

## Usage

This tool is written in ClojureScript. I run it using the
[Lumo](https://github.com/anmonteiro/lumo) ClojureScript implementation, which
runs on top of NodeJS.

First install [NodeJS](https://nodejs.org/), then run the following:

    npm install -g lumo-cljs

To generate the cutlist for a particular board, run the tool as follows:

    ./routes.cljs <boardname>.edn

If you're on Windows, you probably have to run it like this:

    lumo routes.cljs <boardname>.edn

## License

Copyright 2017 John Krasnay

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
