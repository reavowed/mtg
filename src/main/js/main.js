import {render, createElement} from 'preact';
import Game from "./components/game/Game";
import "../css/main.scss";

const container = document.getElementById("container");

render(createElement(Game), container);
