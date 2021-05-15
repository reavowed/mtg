import {h} from 'preact';
import {useState, useEffect} from "preact/hooks";
import _ from 'lodash';
import ScryfallService from "./ScryfallService";
import {getJson} from "./utils/fetch-helpers";

const scryfallService = new ScryfallService();

function Card({card}) {
    const [scryfallCard, setScryfallCard] = useState(null);
    useEffect(() => scryfallService.requestCard(card, setScryfallCard), []);
    return scryfallCard && <img src={scryfallCard.image_uris.small} className="mx-1" />
}

function Hand({objects}) {
    return _.map(objects, object => <Card card={object}/>);
}

export default function Game() {
    const [gameState, setGameState] = useState(null);
    useEffect(() => {
        getJson('$currentPath/state').then(setGameState)
    }, []);

    const content = gameState ? <Hand objects={gameState.hand}/> : <div className="spinner-border"/>;
    return <div class="d-flex flex-column justify-content-center vh-100">
        <div class="d-flex justify-content-center">
            {content}
        </div>
    </div>;
}
