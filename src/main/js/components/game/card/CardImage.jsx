import _ from "lodash";
import {forwardRef} from "preact/compat";
import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../../contexts/ScryfallService";
import {addClass} from "../../../utils/element-utils";

export default forwardRef(function CardImage({card, className, children, showText, ...props}, ref) {
    const scryfallService = useContext(ScryfallService);
    const [artUrl, setArtUrl] = useState(null);
    useEffect(() => scryfallService.requestArtUrl(card.artDetails, setArtUrl), []);
    return artUrl &&
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
                    <img className="cardImage" src={artUrl} />
                    {showText && <div className="cardText">{card.text}</div>}
                    {(card.characteristics && (card.characteristics.power || card.characteristics.toughness)) &&
                        <div className="powerToughnessLozenge">{card.characteristics.power}/{card.characteristics.toughness}</div>
                    }
                    {children}
                </div>
            </div>
        </div>;
});
