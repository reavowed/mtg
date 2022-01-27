import _ from "lodash";
import {createElement} from "preact";
import {useCallback} from "preact/hooks";
import {addClass} from "../../../utils/element-utils";
import {useRefWithEventHandler} from "../../../utils/hook-utils";
import HorizontalCenter from "../../layout/HorizontalCenter";
import CardWithActions from "./CardWithActions";

function CardWrapper({as, ...props}) {
    return <div className="cardWrapper">
        {createElement(as, props)}
    </div>
}

const cardWidth = 129;

export default function CardRow({className, cards, cardProps, as}) {
    as = as || CardWithActions;

    const overlapCards = useCallback((ref) => {
        const cardElements = Array.from(ref.childNodes),
            numberOfCards = cardElements.length;
        if (numberOfCards === 0) return;
        const availableWidth = ref.clientWidth,
            totalCardWidth = cardWidth * cards.length,
            totalWidthToRemove = totalCardWidth - availableWidth,
            individualWidthToRemove = totalWidthToRemove / (cards.length - 1);

        _.forEach(cardElements, (cardElement) => { cardElement.style = {}; });
        if (totalWidthToRemove > 0) {
            _.forEach(cardElements, (cardElement, i) => {
                if (i < cardElements.length - 1) {
                    cardElement.style.width = (cardWidth - individualWidthToRemove) + "px";
                }
                cardElement.style.margin = "0";
            });
        }
    }, [cards]);
    const ref = useRefWithEventHandler(overlapCards, null, [cards]);

    return <div className={addClass(className, "px-2")}>
        <HorizontalCenter ref={ref}>
            {_.map(cards, card => <CardWrapper key={card.objectId} card={card} as={as} {...cardProps} /> )}
        </HorizontalCenter>
    </div>;
}
