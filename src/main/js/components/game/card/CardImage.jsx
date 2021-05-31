import _ from "lodash";
import {forwardRef} from "preact/compat";
import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../../contexts/ScryfallService";
import {addClass} from "../../../utils/element-utils";

export default forwardRef(function CardImage({card, className, children, ...props}, ref) {
    const scryfallService = useContext(ScryfallService);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);
    return scryfallCard &&
        <div className={addClass(className, "cardContainer")} ref={ref} {...props}>
            <div className="cardOuterBorder">
                <div className="cardColorBackground">
                    <div className="cardNameWrapper">
                        <div className="cardNameBorder">
                            <div className="cardName">
                                {card.name}
                            </div>
                        </div>
                    </div>
                    <img className="cardImage" src={scryfallCard.image_uris.art_crop} />
                    {children}
                </div>
            </div>
        </div>;
});
