import { CommonModule } from "@angular/common";
import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { serialize } from "object-to-formdata";
import {
  ApexNonAxisChartSeries,
  ApexResponsive,
  ApexChart,
  ChartComponent,
  NgApexchartsModule,
} from "ng-apexcharts";
import { HttpClient } from '@angular/common/http';

const ALLOWED_FILE_TYPES = [
  'image/jpeg',
  'image/png',
  'image/svg+xml',
];

export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  responsive: ApexResponsive[];
  labels: any;
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, NgApexchartsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  @ViewChild('fileInput', { static: false }) fileInput!: ElementRef;
  @ViewChild("chart") chart!: ChartComponent;

  allowedFileTypes = ALLOWED_FILE_TYPES;
  isUploading = false;
  fileUrl!: string | null;
  uploadFile!: File | null;
  selectedModel!: string | null;
  models!: string[] | null;
  chartOptions!: ChartOptions | null;

  constructor(
    private http: HttpClient
  ) {
  }

  ngOnInit(): void {
    this.http.get("http://localhost:8080/api/inference/list").subscribe((data: any) => {
      this.models = data.map((entry: any) => entry.name)
    })
  }

  handleModelSelect(event: any) {
    if (event.target.value === "Choose a model") {
      this.selectedModel = null;
    } else {
      this.selectedModel = event.target.value;
    }
  }

  handleChange(event: any) {
    const file = event.target.files[0] as File;

    if (this.allowedFileTypes.indexOf(file?.type) === -1) {
      this.handleRemovesFile();
      return;
    }

    this.fileUrl = URL.createObjectURL(file);
    this.uploadFile = file;
  }

  handleRemovesFile() {
    if (this.fileInput && this.fileInput.nativeElement) {
      this.fileInput.nativeElement.value = null;
    }
    this.chartOptions = null;
    this.uploadFile = null;
    this.fileUrl = null;
  }

  handleUploadFile() {
    this.isUploading = true;

    const formData = new FormData()
    
    formData.append('file', this.uploadFile as Blob, this.uploadFile?.name);

    this.http.post(`http://localhost:8080/api/inference/predict/${this.selectedModel}`, formData).subscribe((data: any) => {
      this.chartOptions = {
        series: data.prob,
        chart: {
          width: 380,
          type: "pie"
        },
        labels: data.labels.map((label: string) => label.charAt(0).toUpperCase() + label.slice(1)),
        responsive: [
          {
            breakpoint: 480,
            options: {
              chart: {
                width: 200
              },
              legend: {
                position: "bottom"
              }
            }
          }
        ]
      };
      this.isUploading = false;
    });
  }
}
