<?php

namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;

class NotificationResource extends JsonResource
{
    public function toArray($request): array
    {
        return [
            'id'         => $this->id,
            'type'       => $this->type,
            'data'       => $this->data,
            'read_at'    => optional($this->read_at)?->toISOString(),
            'created_at' => $this->created_at->toISOString(),
        ];
    }
}
