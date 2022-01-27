import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import CardRow from "./card/CardRow";

export default function Hand({cards, ...props}) {
    const gameState = useContext(GameState)
    return <CardRow cards={cards || gameState.hands[gameState.player]} cardProps={{showManaCost: true}} {...props} />;
}
