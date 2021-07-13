
export class AnkiResponseBody {
    public result : any;
    public error: any;

    AnkiResponseBody(result, error) {
        this.result = result;
        this.error = error;
    }
}
