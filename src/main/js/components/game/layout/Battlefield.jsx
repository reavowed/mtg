import _ from "lodash";
import {useContext} from "preact/hooks";
import GameState from "../../../contexts/GameState";
import CardRow from "../card/CardRow";

export default function Battlefield({player, direction}) {
    const gameState = useContext(GameState);

    const creatures = _.filter(gameState.battlefield[player], object => _.includes(object.characteristics.types, "Creature"));
    const noncreatures = _.filter(gameState.battlefield[player], object => !_.includes(object.characteristics.types, "Creature"));

    return direction === "top" ? <div className="d-flex flex-column h-100">
        <CardRow cards={noncreatures} className="my-2"/>
        <CardRow cards={creatures} className="my-2"/>
        <div className="flex-grow-1"/>
    </div> : <div className="d-flex flex-column h-100">
        <div className="flex-grow-1"/>
        <CardRow cards={creatures} className="my-2"/>
        <CardRow cards={noncreatures} className="my-2"/>
    </div>;
}
