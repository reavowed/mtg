import _ from "lodash";
import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import HorizontalCenter from "../layout/HorizontalCenter";
import Card from "./Card";

export default function Hand({cards}) {
    cards = cards || useContext(GameState).hand;
    return <HorizontalCenter className="flex-wrap">
        {_.map(cards, card => <Card key={card.objectId} card={card} className="mx-1" />)}
    </HorizontalCenter>;
}
