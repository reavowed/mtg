import _ from "lodash";
import {createElement} from "preact";
import {useContext} from "preact/hooks";
import GameState from "../../GameState";
import HorizontalCenter from "../layout/HorizontalCenter";
import Card from "./Card";

export default function Hand({cards, as}) {
    as = as || Card;
    cards = cards || useContext(GameState).hand;
    return <HorizontalCenter className="flex-wrap">
        {_.map(cards, card => createElement(as, {key: card.objectId, card, className: "mx-1"}))}
    </HorizontalCenter>;
}
