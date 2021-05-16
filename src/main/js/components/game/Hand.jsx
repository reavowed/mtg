import _ from "lodash";
import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import HorizontalCenter from "../layout/HorizontalCenter";
import Card from "./Card";

export default function Hand() {
    const gameState = useContext(GameState);
    return <HorizontalCenter>
        {_.map(gameState.hand, object => <Card card={object}/>)}
    </HorizontalCenter>;
}
