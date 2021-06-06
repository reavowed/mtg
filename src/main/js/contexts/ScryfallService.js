import _ from "lodash";
import {createContext} from "preact";
import {postJsonAndParseResponse} from "../utils/fetch-helpers";

class ScryfallService {
    knownCards = {};
    requests = [];
    timeoutId = 0;
    requestInProgress = false;
    requestArtUrl(cardDefinition, callback) {
        const knownCard = this.getFromMemory(cardDefinition) || this.getFromLocalStorage(cardDefinition);
        if (knownCard) {
            callback(knownCard.image_uris.art_crop);
        } else {
            const {set, collectorNumber} = cardDefinition;
            const existingRequest = _.find(this.requests, r => r.card.set === set.toLowerCase() && r.collector_number === collectorNumber.toString());
            if (existingRequest) {
                existingRequest.callbacks.push(callback);
            } else {
                this.requests.push({
                    card: {set: set.toLowerCase(), collector_number: collectorNumber.toString()},
                    callbacks: [callback]
                });
                clearTimeout(this.timeoutId);
                this.timeoutId = setTimeout(() => this.makeRequest());
            }
        }
    }
    getFromMemory({set, collectorNumber}) {
        if (this.knownCards[set] && this.knownCards[set][collectorNumber]) {
            return this.knownCards[set][collectorNumber];
        }
    }
    getFromLocalStorage({set, collectorNumber}) {
        const key = `scryfall-${set}-${collectorNumber}`;
        const cardJson = localStorage.getItem(key);
        if (cardJson) {
            const card = JSON.parse(cardJson);
            this.addCardToMemory(card);
            return card;
        }
    }
    makeRequest() {
        if (this.requestInProgress) return;
        this.requestInProgress = true;
        postJsonAndParseResponse("https://api.scryfall.com/cards/collection", {body: {identifiers: _.map(this.requests, "card") }})
            .then(({data}) => _.forEach(data, card => this.addCardFromScryfall(card)));
    }
    addCardFromScryfall(card) {
        const existingRequest = _.find(this.requests, r => r.card.set === card.set && r.card.collector_number === card.collector_number);
        if (existingRequest) {
            _.forEach(existingRequest.callbacks, callback => callback(card.image_uris.art_crop));
        }
        this.addCardToLocalStorage(card);
        this.addCardToMemory(card);
    }
    addCardToLocalStorage(card) {
        const key = `scryfall-${card.set.toUpperCase()}-${card.collector_number}`;
        localStorage.setItem(key, JSON.stringify(card));
    }
    addCardToMemory(card) {
        this.knownCards[card.set.toUpperCase()] ||= {};
        this.knownCards[card.set.toUpperCase()][card.collector_number] = card;
    }
}

export default createContext(new ScryfallService());
