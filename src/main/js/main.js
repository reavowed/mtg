import {render, createElement} from 'preact';
import Game from "./Game";

const container = document.getElementById("container");

render(createElement(Game), container);
