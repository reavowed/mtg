import _ from "lodash";
import {createElement} from "preact";
import {useCallback} from "preact/hooks";
import {addClass} from "../../utils/element-utils";
import {useRefWithEventHandler} from "../../utils/hook-utils";
import HorizontalCenter from "../layout/HorizontalCenter";
import Card from "./Card";

function CardWrapper({card, as}) {
    return <div className="cardWrapper">
        <div className="cardContainer">
            {createElement(as, {card})}
        </div>
    </div>
}

export default function CardRow({className, cards, as}) {
    as = as || Card;

    const overlapCards = useCallback((ref) => {
        const cardElements = Array.from(ref.childNodes),
            numberOfCards = cardElements.length;
        if (numberOfCards == 0) return;
        const imageWidth = cardElements[0].childNodes[0].clientWidth,
            availableWidth = ref.clientWidth,
            totalWidthToRemove = (imageWidth * numberOfCards) - availableWidth,
            individualWidthToRemove = totalWidthToRemove / (cards.length - 1);

        _.forEach(cardElements, (cardElement, i) => { cardElement.style = {}; });
        if (totalWidthToRemove > 0) {
            _.forEach(cardElements, (cardElement, i) => {
                if (i < cardElements.length - 1) {
                    cardElement.style.width = (cardElement.childNodes[0].clientWidth - individualWidthToRemove) + "px";
                }
                cardElement.style.margin = "0";
            });
        }
    }, [cards]);
    const ref = useRefWithEventHandler(overlapCards, null, [cards]);

    return <HorizontalCenter className={addClass(className, "px-2")} ref={ref}>
            {_.map(cards, card => <CardWrapper key={card.objectId} card={card} as={as} /> )}
        </HorizontalCenter>;
}