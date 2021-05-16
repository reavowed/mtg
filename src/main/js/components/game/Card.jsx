import {useContext, useEffect, useState} from "preact/hooks";
import ScryfallService from "../../ScryfallService";

export default function Card({card, ...props}) {
    const scryfallService = useContext(ScryfallService);
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);
    return scryfallCard && <img src={scryfallCard.image_uris.small} {...props} />
}
