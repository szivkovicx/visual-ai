![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Angular](https://img.shields.io/badge/angular-%23DD0031.svg?style=for-the-badge&logo=angular&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)

![image](https://github.com/user-attachments/assets/61c62d24-5d76-4d48-945f-91e2f4ee5e27)

# VisualAI

VisualAI is an mini artificial intelligence project written in Java and Angular that's made for training on custom image data and then object recognition within images. It's flexible and easy to use.

![image](https://github.com/user-attachments/assets/e5580dab-da88-482f-86e4-4c654f0e307d)

## Under the hood

VisualAI consists of 3 projects:

- Model trainer ( `/src/model` directory ) - Mini neural network written in Java controlled completely using a single `conf.json` file ( see `/src/model/conf.example.json` for reference )
- Model API ( `/src/api` directory ) - Mini API written using Spring Boot consisting of 2 routes, one ( `GET` @ `/list` ) written for listing all requests and another one ( `POST` @ `/predict/{model_id}` ) written for fetching probabilities estimated by neural network for each specific label
- Model Web ( `/src/web` directory ) - Mini web application written in Angular made for listing these models and general usage easier

Neural network in VisualAI is as follows:

![image](https://github.com/user-attachments/assets/f88ccc1c-7b30-4f3e-a0f6-92e852ff5c20)

And is controlled by parameters provided in models `conf.json` file which describes basic meta of the model, training parameters, labels and features.

## Example

Example below shows an cat/dog classifing model called `animal-recognizer`. To start, we are first going to train the model using 5 images of dogs and 5 images of cats which we are going to label `cat `and `dog`. We will be training this for 10 epochs with default seed of 1, normalization resolution of 500x500 and learning rate a bit over the usual and average one, 0.005. Configuration file ( `conf.json` ) will look like this:

```json
{
    "name": "animal-recognizer",
    "version": "v0.1",
    "training_parameters": {
        "resolution": [
            500,
            500
        ],
        "learning_rate": 0.005,
        "seed": 1,
        "epochs": 10
    },
    "labels": [
        {
            "id": "dog",
            "data": [
                "../train_data/dog.1.jpg",
                "../train_data/dog.2.jpg",
                "../train_data/dog.3.jpg",
                "../train_data/dog.4.jpg",
                "../train_data/dog.5.jpg"
            ]
        },
        {
            "id": "cat",
            "data": [
                "../train_data/cat.1.jpg",
                "../train_data/cat.2.jpg",
                "../train_data/cat.3.jpg",
                "../train_data/cat.4.jpg",
                "../train_data/cat.5.jpg"
            ]
        }
    ]
}
```

Once we've done that, we can proceed by running `train.cmd` in the terminal. After some time ( depending on machine ), we should see an simple output like this:

![image](https://github.com/user-attachments/assets/244e46d7-3917-4518-bf74-ea3047bf1fed)

Then, we are free to run our API and web application. We are going to run API by going to `/src/api` directory and running `mvnw spring-boot:run`. Then, we can go to `/src/web` and run `npm start`. We should see the following:

![Screen-Recording-2024-09-09T08_55_27 362Z](https://github.com/user-attachments/assets/d4fc028a-142e-4279-bd3a-35668683d2ef)
