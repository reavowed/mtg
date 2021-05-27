import _ from "lodash";
import {forwardRef} from "preact/compat";
import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../contexts/ScryfallService";

export default forwardRef(function Card({card, containerClasses, ...props}, ref) {
    const scryfallService = useContext(ScryfallService);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);
    containerClasses = ["cardContainer", "position-relative", ...(containerClasses || [])];
    if (card.permanentStatus && card.permanentStatus.isTapped) {
        containerClasses.push("tapped")
    }
    if (_.has(card.modifiers, "attacking")) {
        containerClasses.push("attacking");
    }
    if (_.has(card.modifiers, "blocking")) {
        containerClasses.push("blocking");
    }

    return scryfallCard && <div className={containerClasses.join(" ")}>
        <img ref={ref} src={scryfallCard.image_uris.small} {...props} />
        {card.markedDamage > 0 && <span className="position-absolute damage">{card.markedDamage}</span>}
    </div>;
});
