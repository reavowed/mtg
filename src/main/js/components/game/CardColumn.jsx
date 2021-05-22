import _ from "lodash";
import {createElement} from "preact";
import {useCallback} from "preact/hooks";
import {addClass} from "../../utils/element-utils";
import {useRefWithEventHandler} from "../../utils/hook-utils";
import VerticalCenter from "../layout/VerticalCenter";
import Card from "./Card";

function CardWrapper({card, as}) {
    return <div className="cardWrapper">
        {createElement(as, {card})}
    </div>
}

export default function CardColumn({className, cards, as}) {
    as = as || Card;

    const overlapCards = useCallback((ref) => {
        const cardElements = Array.from(ref.childNodes),
            numberOfCards = cardElements.length;
    }, [cards]);
    const ref = useRefWithEventHandler(overlapCards, null, [cards]);

    return <VerticalCenter className={addClass(className, "cardColumn py-2")} ref={ref}>
            {_.map(cards, card => <CardWrapper key={card.objectId} card={card} as={as} /> )}
        </VerticalCenter>;
}
