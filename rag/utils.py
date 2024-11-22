from llama_index.core.schema import Document
from llama_index.core import SimpleDirectoryReader
from pathlib import Path
import tempfile
from urllib.parse import urlparse
from urllib.request import Request, urlopen
import random


def load_data(url_list: list[str]) -> list[Document]:

    documents = []
    with tempfile.TemporaryDirectory() as temp_dir:
        for url in url_list:
            # check the URL
            parsed_url = urlparse(url)

            # Check if the scheme is http or https
            if parsed_url.scheme not in (
                "http",
                "https",
                "ftp",
                "ws",
                "wss",
                "sftp",
                "ftps",
                "s3",
            ):
                raise ValueError(
                    "Invalid URL scheme. Only http, https, ftp, ftps, sftp, ws, wss, and s3 are allowed."
                )

            req = Request(url, headers={"User-Agent": "Magic Browser"})
            result = urlopen(req)

            filename = Path(urlparse(url).path).name

            filepath = f"{temp_dir}/{random.randint(1, 99)}{filename}"
            with open(filepath, "wb") as output:
                output.write(result.read())

        loader = SimpleDirectoryReader(
            temp_dir, file_metadata=(lambda filename: {"file_name": filename})
        )
        documents = loader.load_data()
    return documents
