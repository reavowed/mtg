import _ from "lodash";
import {forwardRef} from "preact/compat";
import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../../contexts/ScryfallService";

export default forwardRef(function CardImage({card, containerClasses, ...props}, ref) {
    const scryfallService = useContext(ScryfallService);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);
    return scryfallCard && <img ref={ref} src={scryfallCard.image_uris.small} {...props} />;
});
