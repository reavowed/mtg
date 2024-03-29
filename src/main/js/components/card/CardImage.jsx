import {forwardRef} from "preact/compat";
import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../contexts/ScryfallService";
import {addClass} from "../../utils/element-utils";
import {FormattedText} from "../FormattedText";
import ManaCost from "./ManaCost";
import _ from "lodash";

export default forwardRef(function CardImage({card, className, children, showManaCost, showText, ...props}, ref) {
    const scryfallService = useContext(ScryfallService);
    const [artUrl, setArtUrl] = useState(null);
    useEffect(() => scryfallService.requestArtUrl(card.artDetails, setArtUrl), []);
    return artUrl &&
        <div className={addClass(className, "cardContainer")} ref={ref} {...props}>
            <div className="cardOuterBorder">
                <div className="cardColorBackground">
                    {showManaCost && card.characteristics.manaCost && <div className="cardManaCost"><ManaCost manaCost={card.characteristics.manaCost}/></div> }
                    <div className="cardNameWrapper">
                        <div className="cardNameBorder">
                            <div className="cardName">
                                {card.name}
                            </div>
                        </div>
                    </div>
                    <img className="cardImage" src={artUrl} />
                    {showText && <div className="cardText"><FormattedText text={card.text}/></div>}
                    {(card.characteristics && _.isNumber(card.characteristics.power) && _.isNumber(card.characteristics.toughness)) &&
                        <div className="powerToughnessLozenge">{card.characteristics.power}/{card.characteristics.toughness}</div>
                    }
                    {children}
                </div>
            </div>
        </div>;
});
