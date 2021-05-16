import {render, createElement} from 'preact';
import Game from "./components/game/Game";

const container = document.getElementById("container");

render(createElement(Game), container);
