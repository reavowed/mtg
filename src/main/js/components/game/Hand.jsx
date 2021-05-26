import {useContext} from "preact/hooks";
import GameState from "../../contexts/GameState";
import CardRow from "./CardRow";

export default function Hand({cards, ...props}) {
    return <CardRow cards={cards || useContext(GameState).hand} {...props} />;
}
